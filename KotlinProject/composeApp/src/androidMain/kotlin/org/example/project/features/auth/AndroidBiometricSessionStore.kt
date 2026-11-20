package org.example.project.features.auth

import android.content.Context
import android.util.AtomicFile
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.crypto.Cipher

internal object AndroidBiometricSessionStore : SessionManager {
    private val mutex = Mutex()
    private var file: AtomicFile? = null
    private var enrollment: BiometricEnvelope? = null
    private val _enabled = MutableStateFlow(false)
    val enabled = _enabled.asStateFlow()

    fun initialize(context: Context) {
        if (file != null) return
        val stored = AtomicFile(File(context.noBackupFilesDir, "biometric-login-v1"))
        file = stored
        enrollment = runCatching { BiometricEnvelope.decode(stored.readFully()) }.getOrNull()
        _enabled.value = enrollment != null
    }

    // The Auth plugin never loads a biometric credential directly. Only the system prompt can authorize decryption.
    override suspend fun loadSession(): UserSession? = null

    override suspend fun saveSession(session: UserSession) = onDisk {
        val saved = enrollment ?: return@onDisk
        val userId = session.user?.id ?: return@onDisk
        if (saved.userId != userId) {
            forget()
        } else if (session.refreshToken.isNotBlank()) {
            // Supabase can rotate the refresh token while the user is signed in.
            // Only the public key is needed to save the new encrypted copy.
            if (runCatching { write(AndroidBiometricVault.encrypt(userId, session.refreshToken)) }.isFailure) {
                forget()
            }
        }
    }

    override suspend fun deleteSession() {
        disable()
    }

    suspend fun prepareEnrollment(userId: String): Pair<BiometricEnvelope, Cipher> = onDisk {
        checkNotNull(file)
        check(enrollment == null)
        AndroidBiometricVault.generateKey()
        val challenge = AndroidBiometricVault.encrypt(userId, "enable-biometric-login")
        challenge to AndroidBiometricVault.decryptCipher()
    }

    suspend fun enable(session: UserSession) = onDisk {
        val userId = requireNotNull(session.user?.id)
        check(session.refreshToken.isNotBlank())
        write(AndroidBiometricVault.encrypt(userId, session.refreshToken))
    }

    suspend fun prepareRestore(): Pair<BiometricEnvelope, Cipher> = onDisk {
        val saved = checkNotNull(enrollment)
        saved to AndroidBiometricVault.decryptCipher()
    }

    suspend fun matches(userId: String): Boolean = onDisk { enrollment?.userId == userId }

    suspend fun disable() = onDisk { forget() }

    private fun forget() {
        if (enrollment == null) return
        file?.delete()
        runCatching { AndroidBiometricVault.deleteKey() }
        enrollment = null
        _enabled.value = false
    }

    private fun write(envelope: BiometricEnvelope) {
        val target = checkNotNull(file)
        val payload = envelope.encode()
        val stream = target.startWrite()
        try {
            stream.write(payload)
            target.finishWrite(stream)
        } catch (error: Exception) {
            target.failWrite(stream)
            throw error
        }
        check(target.readFully().contentEquals(payload))
        enrollment = envelope
        _enabled.value = true
    }

    private suspend fun <T> onDisk(operation: () -> T): T =
        withContext(Dispatchers.IO) { mutex.withLock { operation() } }
}

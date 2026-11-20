package org.example.project.features.auth

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

internal object AndroidBiometricVault {
    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "tachegando-biometric-login-v1"
    private val oaep = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)

    private fun keyStore() = KeyStore.getInstance(KEYSTORE).apply { load(null) }

    fun generateKey() {
        val spec = KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT)
            .setKeySize(2048)
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            spec.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
        } else {
            @Suppress("DEPRECATION")
            spec.setUserAuthenticationValidityDurationSeconds(-1)
        }
        KeyPairGenerator.getInstance("RSA", KEYSTORE).run {
            initialize(spec.build())
            generateKeyPair()
        }
    }

    fun deleteKey() = keyStore().deleteEntry(KEY_ALIAS)

    fun decryptCipher(): Cipher {
        val key = keyStore().getKey(KEY_ALIAS, null) as? PrivateKey ?: error("Missing biometric key")
        return Cipher.getInstance("RSA/ECB/OAEPPadding").apply { init(Cipher.DECRYPT_MODE, key, oaep) }
    }

    fun encrypt(userId: String, token: String): BiometricEnvelope {
        val certificateKey = keyStore().getCertificate(KEY_ALIAS)?.publicKey ?: error("Missing biometric key")
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(certificateKey.encoded))
        return encrypt(userId, token, publicKey)
    }

    fun encrypt(userId: String, token: String, publicKey: PublicKey): BiometricEnvelope {
        val dataKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        val wrap = Cipher.getInstance("RSA/ECB/OAEPPadding").apply { init(Cipher.ENCRYPT_MODE, publicKey, oaep) }
        val encrypt = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, dataKey)
            updateAAD(userId.encodeToByteArray())
        }
        val rawKey = dataKey.encoded
        val rawToken = token.encodeToByteArray()
        return try {
            BiometricEnvelope(userId, wrap.doFinal(rawKey), encrypt.iv, encrypt.doFinal(rawToken))
        } finally {
            rawKey.fill(0)
            rawToken.fill(0)
        }
    }

    fun decrypt(envelope: BiometricEnvelope, authorizedCipher: Cipher): String {
        val rawKey = authorizedCipher.doFinal(envelope.wrappedKey)
        val rawToken = try {
            Cipher.getInstance("AES/GCM/NoPadding").run {
                init(Cipher.DECRYPT_MODE, SecretKeySpec(rawKey, "AES"), GCMParameterSpec(128, envelope.iv))
                updateAAD(envelope.userId.encodeToByteArray())
                doFinal(envelope.ciphertext)
            }
        } finally {
            rawKey.fill(0)
        }
        return try {
            rawToken.decodeToString()
        } finally {
            rawToken.fill(0)
        }
    }
}

internal data class BiometricEnvelope(
    val userId: String,
    val wrappedKey: ByteArray,
    val iv: ByteArray,
    val ciphertext: ByteArray
) {
    fun encode(): ByteArray = listOf(
        userId,
        Base64.encode(wrappedKey),
        Base64.encode(iv),
        Base64.encode(ciphertext)
    ).joinToString("\n").encodeToByteArray()

    companion object {
        fun decode(bytes: ByteArray): BiometricEnvelope {
            require(bytes.size in 1..4096)
            val fields = bytes.decodeToString().split('\n')
            require(fields.size == 4 && fields[0].isNotBlank())
            return BiometricEnvelope(
                fields[0],
                Base64.decode(fields[1]),
                Base64.decode(fields[2]),
                Base64.decode(fields[3])
            ).also {
                require(it.wrappedKey.size == 256 && it.iv.size == 12 && it.ciphertext.size > 16)
            }
        }
    }
}

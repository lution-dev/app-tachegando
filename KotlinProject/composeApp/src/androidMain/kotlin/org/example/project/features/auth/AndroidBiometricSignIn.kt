package org.example.project.features.auth

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.core.data.GoogleLogin
import org.example.project.core.data.supabase
import org.example.project.core.utils.UiState
import javax.crypto.Cipher

internal class AndroidBiometricSignIn(
    private val authorize: suspend (Cipher) -> Cipher?
) : BiometricSignIn {
    private val store = AndroidBiometricSessionStore
    override val enabled = store.enabled

    override suspend fun enable(): UiState<Unit> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return UiState.Error("Entre na sua conta antes de ativar a biometria.")
        return try {
            val (challenge, cipher) = store.prepareEnrollment(userId)
            val authorized = authorize(cipher) ?: return UiState.Error("Ativação biométrica cancelada.")
            val verified = withContext(Dispatchers.IO) {
                AndroidBiometricVault.decrypt(challenge, authorized) == "enable-biometric-login"
            }
            val session = supabase.auth.currentSessionOrNull()
            if (!verified || session?.user?.id != userId) {
                return UiState.Error("A sessão mudou. Ative a biometria novamente.")
            }
            store.enable(session)
            UiState.Success(Unit)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            UiState.Error("Não foi possível ativar a biometria neste aparelho.")
        }
    }

    override suspend fun restore(): UiState<Unit> {
        if (!enabled.value) return UiState.Error("Ative a biometria depois de entrar na conta.")
        GoogleLogin.cancelPending()
        if (supabase.auth.currentSessionOrNull() != null) {
            return UiState.Error("Já existe uma sessão ativa.")
        }
        val (envelope, cipher) = try {
            store.prepareRestore()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            return expired()
        }
        val authorized = authorize(cipher) ?: return UiState.Error("Biometria não confirmada. Tente novamente.")
        val token = try {
            withContext(Dispatchers.IO) { AndroidBiometricVault.decrypt(envelope, authorized) }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            return expired()
        }
        return try {
            val session = supabase.auth.refreshSession(token)
            if (!store.matches(envelope.userId) || supabase.auth.currentSessionOrNull() != null) {
                return UiState.Error("O acesso mudou. Volte para o login e tente novamente.")
            }
            if (session.user?.id != envelope.userId || session.refreshToken.isBlank()) return expired()
            supabase.auth.importSession(session)
            UiState.Success(Unit)
        } catch (error: CancellationException) {
            throw error
        } catch (error: RestException) {
            when (error.statusCode) {
                400, 401, 403 -> expired()
                else -> UiState.Error("O serviço de acesso está indisponível. Tente novamente.")
            }
        } catch (_: Exception) {
            UiState.Error("Não foi possível validar sua sessão. Confira a conexão e tente novamente.")
        }
    }

    override suspend fun disable(): UiState<Unit> {
        return try {
            store.disable()
            UiState.Success(Unit)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            UiState.Error("Não foi possível remover a biometria. Tente novamente.")
        }
    }

    private suspend fun expired(): UiState<Unit> {
        val removal = disable()
        if (removal is UiState.Error) return removal
        return UiState.Error("O acesso biométrico salvo expirou. Entre com sua conta e ative-o novamente.")
    }
}

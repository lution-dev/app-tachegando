package org.example.project.core.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.example.project.core.utils.UiState

object AuthRepository {
    val sessionStatus get() = supabase.auth.sessionStatus

    suspend fun signUp(
        emailValue: String,
        passwordValue: String,
        nameValue: String? = null,
        roleValue: String? = null,
        businessNameValue: String? = null
    ): UiState<Unit> {
        return try {
            supabase.auth.signUpWith(Email) {
                email = emailValue
                password = passwordValue
                if (nameValue != null || roleValue != null || businessNameValue != null) {
                    data = kotlinx.serialization.json.buildJsonObject {
                        if (nameValue != null) put("name", kotlinx.serialization.json.JsonPrimitive(nameValue))
                        if (nameValue != null) put("full_name", kotlinx.serialization.json.JsonPrimitive(nameValue))
                        if (businessNameValue != null) put("business_name", kotlinx.serialization.json.JsonPrimitive(businessNameValue))
                        if (roleValue != null) put("role", kotlinx.serialization.json.JsonPrimitive(roleValue))
                    }
                }
            }
            UiState.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(handleError(e))
        }
    }

    suspend fun signIn(emailValue: String, passwordValue: String): UiState<Unit> {
        return try {
            GoogleLogin.cancelPending()
            supabase.auth.signInWith(Email) {
                email = emailValue
                password = passwordValue
            }
            UiState.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(handleError(e, "Não foi possível entrar. Tente novamente."))
        }
    }

    suspend fun signOut(): UiState<Unit> {
        return try {
            supabase.auth.signOut(SignOutScope.LOCAL)
            UiState.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error("Erro ao sair. Tente novamente.")
        }
    }

    suspend fun verifyCurrentSession(expectedUserId: String): UiState<String> {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            if (user.id != expectedUserId || supabase.auth.currentUserOrNull()?.id != user.id) {
                return UiState.Error("A sessão mudou. Entre novamente.")
            }
            // O metadado preserva a escolha de navegação do cadastro. A autorização é via RLS.
            val role = (user.userMetadata?.get("role") as? JsonPrimitive)?.contentOrNull
            val isVendor = role == "vendor" || VendorRepository.getByUserId(user.id) != null
            UiState.Success(if (isVendor) "vendor" else "consumer")
        } catch (e: CancellationException) {
            throw e
        } catch (e: RestException) {
            if ((e.statusCode == 401 || e.statusCode == 403) &&
                supabase.auth.currentUserOrNull()?.id == expectedUserId
            ) {
                supabase.auth.clearSession()
            }
            UiState.Error("Não foi possível validar a sessão. Tente novamente.")
        } catch (e: Exception) {
            UiState.Error("Não foi possível validar a sessão. Verifique sua conexão.")
        }
    }

    suspend fun resetPassword(emailValue: String): UiState<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(emailValue)
            UiState.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(handleError(e, "Não foi possível enviar o link. Tente novamente."))
        }
    }

    private fun handleError(
        e: Exception,
        fallback: String = "Não foi possível concluir o cadastro. Verifique os dados e tente novamente."
    ): String {
        val msg = e.message?.lowercase() ?: ""
        return when {
            msg.contains("invalid login credentials") || msg.contains("invalid_credentials") -> "Email ou senha incorretos."
            msg.contains("user already registered") -> "Este email já está cadastrado."
            msg.contains("password should be") -> "A senha é muito fraca (mínimo 6 caracteres)."
            msg.contains("network") || msg.contains("timeout") || msg.contains("host") -> "Sem conexão com a internet."
            msg.contains("too many requests") || msg.contains("rate_limit") || msg.contains("over_email_send_rate_limit") -> "Você tentou muitas vezes. Aguarde alguns minutos e tente novamente."
            else -> fallback
        }
    }
}

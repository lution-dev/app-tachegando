package org.example.project.core.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.example.project.core.utils.UiState

object AuthRepository {
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
        } catch (e: Exception) {
            UiState.Error(handleError(e))
        }
    }

    suspend fun signIn(emailValue: String, passwordValue: String): UiState<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                email = emailValue
                password = passwordValue
            }
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(handleError(e))
        }
    }

    suspend fun signOut(): UiState<Unit> {
        return try {
            supabase.auth.signOut()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error("Erro ao sair. Tente novamente.")
        }
    }

    fun currentUser() = supabase.auth.currentUserOrNull()

    fun currentUserRole(): String {
        return currentUserMetadataRole() ?: "consumer"
    }

    suspend fun resolveCurrentUserRole(): String {
        val user = currentUser() ?: return "consumer"
        if (VendorRepository.getByUserId(user.id) != null) return "vendor"
        return currentUserMetadataRole() ?: "consumer"
    }

    suspend fun resetPassword(emailValue: String): UiState<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(emailValue)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(handleError(e))
        }
    }

    private fun currentUserMetadataRole(): String? {
        return currentUser()
            ?.userMetadata
            ?.get("role")
            ?.jsonPrimitive
            ?.contentOrNull
    }

    private fun handleError(e: Exception): String {
        println("AuthRepository error: ${e::class.simpleName}: ${e.message}")
        val msg = e.message?.lowercase() ?: ""
        return when {
            msg.contains("invalid login credentials") || msg.contains("invalid_credentials") -> "Email ou senha incorretos."
            msg.contains("user already registered") -> "Este email já está cadastrado."
            msg.contains("email not confirmed") || msg.contains("email_not_confirmed") -> "Confirme seu email antes de entrar."
            msg.contains("password should be") -> "A senha é muito fraca (mínimo 6 caracteres)."
            msg.contains("too many requests") || msg.contains("rate_limit") || msg.contains("over_email_send_rate_limit") -> "Você tentou muitas vezes. Aguarde alguns minutos e tente novamente."
            isNetworkError(e) -> "Sem conexão com a internet."
            else -> "Não foi possível concluir a operação. Verifique os dados e tente novamente."
        }
    }

    // Só considera falta de internet quando a falha é de transporte, não por texto solto na mensagem.
    private fun isNetworkError(e: Throwable): Boolean {
        val names = generateSequence(e) { it.cause }.mapNotNull { it::class.simpleName }
        return names.any {
            it in setOf(
                "UnknownHostException",
                "ConnectException",
                "SocketException",
                "SocketTimeoutException",
                "NoRouteToHostException",
                "HttpRequestTimeoutException",
                "ConnectTimeoutException"
            )
        }
    }
}

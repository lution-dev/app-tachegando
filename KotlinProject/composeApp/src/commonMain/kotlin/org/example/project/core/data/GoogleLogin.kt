package org.example.project.core.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.ktor.http.Url
import io.ktor.http.parseQueryString
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal expect val supportsGoogleLogin: Boolean

internal const val GOOGLE_REDIRECT_URL = "br.com.tachegandoapp://auth"

internal object GoogleLogin {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutex = Mutex()
    private var pending = false
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _exchanging = MutableStateFlow(false)
    val exchanging = _exchanging.asStateFlow()

    suspend fun start() = mutex.withLock {
        _error.value = null
        pending = true
        try {
            supabase.auth.awaitInitialization()
            supabase.auth.signInWith(Google, redirectUrl = GOOGLE_REDIRECT_URL)
        } catch (e: CancellationException) {
            pending = false
            throw e
        } catch (_: Exception) {
            pending = false
            _error.value = "Não foi possível abrir o login do Google. Tente novamente."
        }
    }

    suspend fun cancelPending() = mutex.withLock {
        _error.value = null
        if (pending) {
            pending = false
            supabase.auth.codeVerifierCache.deleteCodeVerifier()
        }
    }

    fun handleRedirect(value: String) {
        val callback = parseGoogleCallback(value) ?: return
        scope.launch {
            mutex.withLock {
                if (!pending) return@withLock
                pending = false
                if (callback is GoogleCallback.Failed) {
                    supabase.auth.codeVerifierCache.deleteCodeVerifier()
                    _error.value = if (callback.cancelled) "Login com Google cancelado."
                    else "O Google não concluiu o login. Tente novamente."
                    return@withLock
                }
                _exchanging.value = true
                try {
                    val code = (callback as GoogleCallback.Code).value
                    supabase.auth.exchangeCodeForSession(code)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _error.value = "Não foi possível concluir o login com Google. Tente novamente."
                } finally {
                    supabase.auth.codeVerifierCache.deleteCodeVerifier()
                    _exchanging.value = false
                }
            }
        }
    }
}

internal sealed interface GoogleCallback {
    data class Code(val value: String) : GoogleCallback
    data class Failed(val cancelled: Boolean) : GoogleCallback
}

internal fun parseGoogleCallback(value: String): GoogleCallback? {
    val endpoint = value.substringBefore('?').substringBefore('#')
    if (endpoint != GOOGLE_REDIRECT_URL && endpoint != "$GOOGLE_REDIRECT_URL/") return null
    val url = try {
        Url(value)
    } catch (_: IllegalArgumentException) {
        return null
    }
    val fragment = parseQueryString(url.fragment)
    val error = url.parameters["error"] ?: fragment["error"]
    if (error != null) return GoogleCallback.Failed(cancelled = error == "access_denied")
    val code = url.parameters.getAll("code")?.singleOrNull()?.takeIf { it.isNotBlank() }
    return code?.let { GoogleCallback.Code(it) } ?: GoogleCallback.Failed(cancelled = false)
}

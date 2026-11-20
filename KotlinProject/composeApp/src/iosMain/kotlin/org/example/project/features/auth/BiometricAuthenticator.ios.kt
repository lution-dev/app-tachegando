package org.example.project.features.auth

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

@Composable
internal actual fun rememberBiometricAuthenticator(): BiometricAuthenticator? = IosBiometricAuthenticator

@OptIn(ExperimentalForeignApi::class)
private object IosBiometricAuthenticator : BiometricAuthenticator {
    private val policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics

    override fun availability() = if (LAContext().canEvaluatePolicy(policy, error = null)) {
        BiometricAvailability.Available
    } else {
        BiometricAvailability.Unsupported
    }

    override suspend fun authenticate(): Boolean {
        val context = LAContext()
        if (!context.canEvaluatePolicy(policy, error = null)) return false
        context.localizedFallbackTitle = ""
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { context.invalidate() }
            context.evaluatePolicy(policy, localizedReason = "Desbloquear sua sessão no Tá Chegando") { success, _ ->
                if (continuation.isActive) continuation.resume(success)
            }
        }
    }
}

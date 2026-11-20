package org.example.project.features.auth

import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.crypto.Cipher
import kotlin.coroutines.resume

@Composable
internal actual fun rememberBiometricAuthenticator(): BiometricAuthenticator? {
    val activity = LocalActivity.current as? FragmentActivity ?: return null
    return remember(activity) { AndroidBiometricAuthenticator(activity) }
}

private class AndroidBiometricAuthenticator(private val activity: FragmentActivity) : BiometricAuthenticator {
    override val signIn = AndroidBiometricSignIn { cipher ->
        authenticatePrompt(cipher)?.cryptoObject?.cipher
    }

    override fun availability() =
        androidBiometricAvailability(BiometricManager.from(activity).canAuthenticate(BIOMETRIC_STRONG))

    override suspend fun authenticate(): Boolean = authenticatePrompt(null) != null

    private suspend fun authenticatePrompt(cipher: Cipher?): BiometricPrompt.AuthenticationResult? {
        if (availability() != BiometricAvailability.Available) return null
        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(activity)
            val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) continuation.resume(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (continuation.isActive) continuation.resume(null)
                }
            })
            continuation.invokeOnCancellation { executor.execute { prompt.cancelAuthentication() } }
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirme sua biometria")
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .setNegativeButtonText("Cancelar")
                .build()
            try {
                if (cipher == null) prompt.authenticate(info)
                else prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
            } catch (_: IllegalStateException) {
                if (continuation.isActive) continuation.resume(null)
            }
        }
    }
}

internal fun androidBiometricAvailability(status: Int): BiometricAvailability = when (status) {
    // O prompt continua exigindo biometria forte mesmo quando o Android não consegue prever a disponibilidade.
    BiometricManager.BIOMETRIC_SUCCESS,
    BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> BiometricAvailability.Available
    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NotEnrolled
    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.SensorUnavailable
    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SecurityUpdateRequired
    else -> BiometricAvailability.Unsupported
}

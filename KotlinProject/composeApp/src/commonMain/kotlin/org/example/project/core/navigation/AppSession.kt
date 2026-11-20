package org.example.project.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import org.example.project.core.data.AuthRepository
import org.example.project.core.utils.UiState
import org.example.project.features.auth.BiometricAvailability
import org.example.project.features.auth.BiometricEntry
import org.example.project.features.auth.BiometricLockOption
import org.example.project.features.auth.BiometricSignInSettings
import org.example.project.features.auth.BiometricSessionLock
import org.example.project.features.auth.rememberBiometricAuthenticator

private val biometricLock = BiometricSessionLock()

@Composable
fun AppNavigation() {
    val status by AuthRepository.sessionStatus.collectAsState()
    val authenticated = status as? SessionStatus.Authenticated
    val userId = authenticated?.session?.user?.id
    val scope = rememberCoroutineScope()
    val biometrics = rememberBiometricAuthenticator()
    val biometricSignIn = biometrics?.signIn
    val savedBiometricSignIn = biometricSignIn?.enabled?.collectAsState()?.value == true
    val startAtBiometricLogin = remember { savedBiometricSignIn }
    val navState = rememberSaveableStateHolder()
    var navAccountId by remember { mutableStateOf<String?>(null) }
    var biometricAvailability by remember(biometrics) {
        mutableStateOf(biometrics?.availability() ?: BiometricAvailability.Unsupported)
    }
    var unlocking by remember(userId) { mutableStateOf(false) }
    var unlockError by remember(userId) { mutableStateOf<String?>(null) }
    var signedOutExplicitly by remember { mutableStateOf(false) }
    var leaving by remember { mutableStateOf(false) }
    var logoutError by remember { mutableStateOf<String?>(null) }
    var managingBiometrics by remember(userId) { mutableStateOf(false) }
    var biometricSettingMessage by remember(userId) { mutableStateOf<String?>(null) }
    var retry by remember { mutableStateOf(0) }
    var startRoute by remember(userId) { mutableStateOf<String?>(null) }
    var validationError by remember(userId, retry) { mutableStateOf<String?>(null) }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { biometricLock.onBackground() }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        biometricAvailability = biometrics?.availability() ?: BiometricAvailability.Unsupported
    }
    LaunchedEffect(userId, status is SessionStatus.NotAuthenticated) {
        if (userId != null || status is SessionStatus.NotAuthenticated) {
            biometricLock.onAccountChanged(userId)
            if (navAccountId != userId) {
                navAccountId?.let(navState::removeState)
                navAccountId = userId
            }
        }
    }

    LaunchedEffect(userId, retry) {
        if (userId == null || startRoute != null) return@LaunchedEffect
        signedOutExplicitly = false
        when (val result = AuthRepository.verifyCurrentSession(userId)) {
            is UiState.Success -> {
                startRoute = when {
                    result.data != "vendor" -> NavDestination.ConsumerHome.route
                    authenticated.source is SessionSource.SignUp -> NavDestination.VendorSubscription.route
                    else -> NavDestination.VendorDashboard.route
                }
            }
            is UiState.Error -> validationError = result.message
            UiState.Loading -> Unit
        }
    }

    suspend fun signOut(): UiState<Unit> {
        if (leaving || managingBiometrics) return UiState.Loading
        leaving = true
        signedOutExplicitly = true
        return try {
            AuthRepository.signOut().also { result ->
                logoutError = (result as? UiState.Error)?.message
                if (result is UiState.Error) signedOutExplicitly = false
                if (result is UiState.Success) {
                    biometricLock.clear()
                    if (userId != null) navState.removeState(userId)
                }
            }
        } finally {
            leaving = false
        }
    }
    val returnToLogin: () -> Unit = { scope.launch { signOut() } }

    when (val sessionStatus = status) {
        SessionStatus.Initializing -> SessionFeedback("Carregando sessão…")
        is SessionStatus.RefreshFailure -> SessionFeedback(
            message = logoutError ?: "Sem conexão com o serviço de autenticação. Tentando novamente…",
            busy = leaving,
            onLogout = returnToLogin
        )
        is SessionStatus.NotAuthenticated -> {
            val route = if (sessionStatus.isSignOut || startAtBiometricLogin) NavDestination.Login.route
                else NavDestination.Splash.route
            AppGraph(
                startRoute = route,
                userId = null,
                sessionMessage = if (sessionStatus.isSignOut && !signedOutExplicitly)
                    "Sua sessão terminou. Entre novamente." else null,
                biometricEntry = biometricSignIn?.let {
                    BiometricEntry(savedBiometricSignIn, biometricAvailability, it::restore)
                },
            )
        }
        is SessionStatus.Authenticated -> {
            val verifiedStart = startRoute
            if (verifiedStart != null && userId != null) {
                if (biometricLock.isLocked(userId)) {
                    val unlock: (() -> Unit)? = biometrics
                        ?.takeIf { biometricAvailability == BiometricAvailability.Available }?.let { authenticator ->
                        {
                            val attempt = biometricLock.beginUnlock(userId)
                            if (!unlocking && attempt != null) {
                                unlocking = true
                                scope.launch {
                                    try {
                                        unlockError = null
                                        val confirmed = authenticator.authenticate()
                                        val verification = if (confirmed) AuthRepository.verifyCurrentSession(userId) else null
                                        biometricLock.completeUnlock(userId, attempt, confirmed, verification is UiState.Success)
                                        biometricAvailability = authenticator.availability()
                                        unlockError = when {
                                            verification is UiState.Error -> verification.message
                                            !confirmed -> biometricAvailability.message
                                                ?: "Biometria não confirmada. Tente novamente ou entre sem biometria."
                                            biometricLock.isLocked(userId) ->
                                                "O app voltou ao segundo plano. Tente desbloquear novamente."
                                            else -> null
                                        }
                                    } finally {
                                        unlocking = false
                                    }
                                }
                            }
                        }
                    }
                    SessionFeedback(
                        message = logoutError ?: unlockError ?: biometricAvailability.message ?: "Sessão bloqueada",
                        busy = leaving || unlocking,
                        onRetry = unlock,
                        retryLabel = "Desbloquear com biometria",
                        retryIcon = FingerprintIcon,
                        onLogout = returnToLogin,
                        logoutLabel = "Entrar sem biometria"
                    )
                } else {
                    navState.SaveableStateProvider(userId) {
                        val settings: (@Composable () -> Unit)? = when {
                            biometricSignIn != null -> {
                                {
                                    BiometricSignInSettings(
                                        enabled = savedBiometricSignIn,
                                        availability = biometricAvailability,
                                        busy = managingBiometrics || leaving,
                                        message = biometricSettingMessage,
                                        onChange = { enabled ->
                                            if (!managingBiometrics && !leaving) {
                                                managingBiometrics = true
                                                biometricSettingMessage = null
                                                scope.launch {
                                                    try {
                                                        val result = if (enabled) biometricSignIn.enable()
                                                            else biometricSignIn.disable()
                                                        biometricSettingMessage = (result as? UiState.Error)?.message
                                                    } finally {
                                                        managingBiometrics = false
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            biometrics != null -> {
                                {
                                    BiometricLockOption(
                                        enabled = biometricLock.isEnabled(userId),
                                        availability = biometricAvailability,
                                        onChange = { enabled ->
                                            biometricAvailability = biometrics.availability()
                                            biometricLock.setEnabled(
                                                userId, enabled, biometricAvailability == BiometricAvailability.Available
                                            )
                                        },
                                        onLockNow = {
                                            biometricAvailability = biometrics.availability()
                                            if (biometricAvailability == BiometricAvailability.Available) {
                                                unlockError = null
                                                logoutError = null
                                                biometricLock.lock(userId)
                                            }
                                        }
                                    )
                                }
                            }
                            else -> null
                        }
                        AppGraph(
                            verifiedStart,
                            userId,
                            sessionMessage = null,
                            biometricSettings = settings
                        )
                    }
                }
            } else if (validationError != null || userId == null) {
                SessionFeedback(
                    message = logoutError ?: validationError ?: "Não foi possível identificar esta sessão.",
                    busy = leaving,
                    onRetry = if (userId != null) ({ retry++ }) else null,
                    onLogout = returnToLogin
                )
            } else {
                SessionFeedback("Validando sessão…")
            }
        }
    }
}

@Composable
private fun SessionFeedback(
    message: String,
    busy: Boolean = false,
    onRetry: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    retryLabel: String = "Tentar novamente",
    retryIcon: ImageVector? = null,
    logoutLabel: String = "Voltar para o login"
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        if (busy || onRetry == null && onLogout == null) CircularProgressIndicator()
        Text(message, textAlign = TextAlign.Center)
        if (onRetry != null) {
            Button(onClick = onRetry, enabled = !busy) {
                if (retryIcon != null) {
                    Icon(retryIcon, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                }
                Text(retryLabel)
            }
        }
        if (onLogout != null) {
            TextButton(onClick = onLogout, enabled = !busy) { Text(logoutLabel) }
        }
    }
}

package org.example.project.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.example.project.core.navigation.FingerprintIcon
import org.example.project.core.utils.UiState

internal enum class BiometricAvailability(val message: String?) {
    Available(null),
    NotEnrolled("Cadastre uma biometria compatível nas configurações do aparelho."),
    SensorUnavailable("Sensor biométrico indisponível no momento. Tente novamente."),
    SecurityUpdateRequired("Atualize o sistema para voltar a usar a biometria."),
    Unsupported("Biometria compatível indisponível neste aparelho.")
}

internal interface BiometricAuthenticator {
    val signIn: BiometricSignIn? get() = null
    fun availability(): BiometricAvailability
    suspend fun authenticate(): Boolean
}

internal interface BiometricSignIn {
    val enabled: StateFlow<Boolean>
    suspend fun enable(): UiState<Unit>
    suspend fun disable(): UiState<Unit>
    suspend fun restore(): UiState<Unit>
}

internal data class BiometricEntry(
    val enabled: Boolean,
    val availability: BiometricAvailability,
    val restore: suspend () -> UiState<Unit>
)

@Composable
internal expect fun rememberBiometricAuthenticator(): BiometricAuthenticator?

internal class BiometricSessionLock {
    private var enabledUserId by mutableStateOf<String?>(null)
    private var locked by mutableStateOf(false)
    private var revision = 0L

    fun isEnabled(userId: String) = enabledUserId == userId
    fun isLocked(userId: String) = isEnabled(userId) && locked

    fun setEnabled(userId: String, enabled: Boolean, available: Boolean) {
        if (!enabled) {
            clear()
        } else if (available && !isEnabled(userId)) {
            enabledUserId = userId
            locked = false
        }
    }

    fun onBackground() {
        enabledUserId?.let(::lock)
    }

    fun lock(userId: String) {
        if (isEnabled(userId)) {
            revision++
            locked = true
        }
    }

    fun onAccountChanged(userId: String?) {
        if (enabledUserId != userId) clear()
    }

    fun beginUnlock(userId: String): Long? = revision.takeIf { isLocked(userId) }

    fun completeUnlock(userId: String, attempt: Long, biometricConfirmed: Boolean, sessionConfirmed: Boolean) {
        if (attempt == revision && isLocked(userId) && biometricConfirmed && sessionConfirmed) {
            locked = false
        }
    }

    fun clear() {
        revision++
        enabledUserId = null
        locked = false
    }
}

@Composable
internal fun BiometricLockOption(
    enabled: Boolean,
    availability: BiometricAvailability,
    onChange: (Boolean) -> Unit,
    onLockNow: () -> Unit
) {
    val available = availability == BiometricAvailability.Available
    val canChange = available || enabled
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().toggleable(
                value = enabled,
                enabled = canChange,
                role = Role.Switch,
                onValueChange = onChange
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(FingerprintIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text("Bloqueio biométrico ao sair do app", modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = null, enabled = canChange)
        }
        availability.message?.let { message ->
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (enabled && available) {
            TextButton(onClick = onLockNow) { Text("Bloquear agora") }
        }
    }
}

@Composable
internal fun BiometricSignInSettings(
    enabled: Boolean,
    availability: BiometricAvailability,
    busy: Boolean,
    message: String?,
    onChange: (Boolean) -> Unit
) {
    val canChange = !busy && (enabled || availability == BiometricAvailability.Available)
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().toggleable(
                value = enabled,
                enabled = canChange,
                role = Role.Switch,
                onValueChange = onChange
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(FingerprintIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text("Entrar com biometria", modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = null, enabled = canChange)
        }
        Text(
            message ?: availability.message
                ?: if (enabled) "Ativada para esta conta neste aparelho."
                else "Use a biometria do aparelho nas próximas entradas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

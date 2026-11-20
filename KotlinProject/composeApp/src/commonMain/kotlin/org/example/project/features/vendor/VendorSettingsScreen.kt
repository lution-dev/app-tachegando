package org.example.project.features.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.core.data.CatalogRepository
import org.example.project.core.data.PhotoChange
import org.example.project.core.data.VendorMapProfile
import org.example.project.core.data.VendorTransport
import org.example.project.core.data.toPhotoBytes
import org.example.project.core.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorSettingsScreen(
    userId: String,
    onSubscriptionClick: () -> Unit,
    biometricSettings: (@Composable () -> Unit)?
) {
    var reload by remember { mutableStateOf(0) }
    val profile by produceState<UiState<VendorMapProfile>>(UiState.Loading, userId, reload) {
        value = CatalogRepository.profile()
    }
    Scaffold(topBar = { TopAppBar(title = { Text("Minha conta") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onSubscriptionClick, modifier = Modifier.fillMaxWidth()) {
                Text("Meu plano")
            }
            biometricSettings?.invoke()
            HorizontalDivider()
            Text("Perfil no mapa", style = MaterialTheme.typography.titleLarge)
            Text(
                "A foto e o meio de transporte são opcionais e aparecem para clientes enquanto sua rota estiver ativa.",
                style = MaterialTheme.typography.bodyMedium
            )
            when (val result = profile) {
                UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> {
                    Text(result.message, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { reload++ }) { Text("Tentar novamente") }
                }
                is UiState.Success -> VendorMapProfileEditor(result.data)
            }
        }
    }
}

@Composable
private fun VendorMapProfileEditor(profile: VendorMapProfile) {
    val scope = rememberCoroutineScope()
    var original by remember(profile) { mutableStateOf(profile.avatar?.toPhotoBytes()) }
    var originalTransport by remember(profile) { mutableStateOf(profile.transport) }
    var transport by remember(profile) { mutableStateOf(profile.transport) }
    var photoChange by remember(profile) { mutableStateOf<PhotoChange?>(null) }
    var open by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<UiState<Unit>?>(null) }

    PhotoEditor("Sua foto (opcional)", original, photoChange, saving) { photoChange = it; feedback = null }
    Column {
        Text("Meio de transporte (opcional)", style = MaterialTheme.typography.titleSmall)
        OutlinedButton(onClick = { open = true }, enabled = !saving, modifier = Modifier.fillMaxWidth()) {
            Text(transport?.label ?: "Selecionar meio de transporte")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(
                text = { Text("Não informar") },
                onClick = { transport = null; open = false; feedback = null }
            )
            VendorTransport.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = { transport = option; open = false; feedback = null }
                )
            }
        }
    }
    Button(
        enabled = !saving && (photoChange != null || transport != originalTransport),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            saving = true
            scope.launch {
                try {
                    feedback = CatalogRepository.saveProfile(transport, photoChange)
                    if (feedback is UiState.Success) {
                        if (photoChange != null) original = photoChange?.bytes
                        photoChange = null
                        originalTransport = transport
                    }
                } finally {
                    saving = false
                }
            }
        }
    ) {
        Text(if (saving) "Salvando…" else "Salvar perfil no mapa")
    }
    when (val result = feedback) {
        is UiState.Success -> Text("Perfil do mapa atualizado.")
        is UiState.Error -> Text(result.message, color = MaterialTheme.colorScheme.error)
        else -> Unit
    }
}

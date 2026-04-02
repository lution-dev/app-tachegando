package org.example.project.features.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    onStartRoute: () -> Unit
) {
    var isOnline by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Painel do Vendedor") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isOnline) "Você está ONLINE" else "Você está OFFLINE",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isOnline,
                    onCheckedChange = { isOnline = it }
                )
            }
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(title = "Visitas Hoje", value = "12", modifier = Modifier.weight(1f))
                StatCard(title = "Clientes Próximos", value = "45", modifier = Modifier.weight(1f))
            }
            
            Button(
                onClick = onStartRoute,
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                enabled = isOnline
            ) {
                Text("Iniciar Rota / Compartilhar Localização")
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.displayMedium)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

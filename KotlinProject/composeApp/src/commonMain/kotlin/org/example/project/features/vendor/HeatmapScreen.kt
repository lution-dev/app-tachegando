package org.example.project.features.vendor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import org.example.project.core.utils.Location
import org.example.project.core.ui.maps.HeatZoneLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen() {
    // Coordenadas simuladas em São Paulo para zonas de calor
    val heatZones = listOf(
        HeatZoneLocation(Location(-23.5505, -46.6333), 0.95f, 1500.0), // Centro
        HeatZoneLocation(Location(-23.5015, -46.6253), 0.7f, 1200.0),  // Norte
        HeatZoneLocation(Location(-23.5985, -46.6763), 0.55f, 1000.0), // Sul
        HeatZoneLocation(Location(-23.5405, -46.5400), 0.8f, 1300.0),  // Leste
        HeatZoneLocation(Location(-23.5415, -46.7000), 0.4f, 900.0)    // Oeste
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Onde tem clientes") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Background Map with Heat Zones
            org.example.project.core.ui.maps.NativeMap(
                modifier = Modifier.fillMaxSize(),
                userLocation = Location(-23.5505, -46.6333), // Centralizado em SP
                heatZones = heatZones,
                onMapClick = {}
            )

            // Small legend at the bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Onde tem mais clientes?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "As manchas em vermelho mostram onde tem mais clientes procurando produtos agora.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

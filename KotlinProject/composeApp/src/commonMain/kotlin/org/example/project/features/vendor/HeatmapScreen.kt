package org.example.project.features.vendor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class HeatZone(val name: String, val intensity: Float, val customers: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen() {
    var selectedZone by remember { mutableStateOf<HeatZone?>(null) }

    val zones = listOf(
        HeatZone("Centro", 0.95f, 45),
        HeatZone("Norte", 0.7f, 32),
        HeatZone("Sul", 0.55f, 28),
        HeatZone("Leste", 0.8f, 20),
        HeatZone("Oeste", 0.4f, 15)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mapa de Calor da Demanda") })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "Toque em uma zona para ver detalhes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(zones) { zone ->
                    val isSelected = selectedZone == zone
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                Color.Black.copy(alpha = zone.intensity * 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedZone = zone }
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                zone.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "${zone.customers} clientes pr\u00f3ximos",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedZone != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Zona: ${selectedZone!!.name}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Intensidade: ${(selectedZone!!.intensity * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Clientes pr\u00f3ximos: ${selectedZone!!.customers}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Dica: boas oportunidades de venda nessa regi\u00e3o!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Selecione uma zona acima",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

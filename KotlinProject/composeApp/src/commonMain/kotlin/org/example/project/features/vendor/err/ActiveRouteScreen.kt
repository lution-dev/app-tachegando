package org.example.project.features.vendor.err

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.project.core.domain.models.DemandCluster
import org.example.project.core.data.RouteRepository
import org.example.project.core.ui.maps.NativeMap
import org.example.project.core.utils.Location

@Composable
fun ActiveRouteScreen(
    routeId: String,
    onStopRoute: () -> Unit
) {
    val engine = remember { ERREngine(RouteRepository()) }
    var clusters by remember { mutableStateOf<List<DemandCluster>>(emptyList()) }

    LaunchedEffect(routeId) {
        while (true) {
            try {
                clusters = engine.processIntents(routeId)
            } catch (e: Exception) {
                // Previne crash silencioso
            }
            delay(30000) // Poll a cada 30s
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NativeMap(
            modifier = Modifier.fillMaxSize(),
            userLocation = Location(-23.5505, -46.6333) // Mock GPS location
        )

        // Botão de voltar/encerrar no topo
        Button(
            onClick = onStopRoute,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .padding(top = 32.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Parar", fontWeight = FontWeight.Bold)
        }

        // Painel flutuante de Desvios Sugeridos
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.45f) // Ocupa os 45% inferiores
                .padding(8.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Clientes perto da rota",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "Procurando clientes por perto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(clusters) { cluster ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${cluster.intentCount} clientes juntos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("+${cluster.estimatedDetourSeconds / 60} min", style = MaterialTheme.typography.labelMedium)
                                }
                                Text("A ${cluster.perpendicularDistanceMeters.toInt()}m do seu caminho", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { /* Navegar para o cluster */ },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Ir até eles")
                                }
                            }
                        }
                    }
                    if (clusters.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Ainda não há clientes perto da sua rota.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

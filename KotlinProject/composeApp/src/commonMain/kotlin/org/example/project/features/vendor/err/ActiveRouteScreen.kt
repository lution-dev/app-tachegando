package org.example.project.features.vendor.err

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.example.project.core.domain.models.DemandCluster
import org.example.project.core.data.RouteRepository
import org.example.project.core.data.CatalogRepository
import org.example.project.core.ui.maps.NativeMap
import org.example.project.core.utils.Location
import org.example.project.core.utils.UiState
import org.example.project.core.utils.rememberLocationTracker

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ActiveRouteScreen(
    routeId: String,
    onStopRoute: () -> Unit
) {
    val engine = remember { ERREngine(RouteRepository()) }
    var clusters by remember { mutableStateOf<List<DemandCluster>>(emptyList()) }
    val tracker = rememberLocationTracker()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var currentLocation by remember(routeId) { mutableStateOf<Location?>(null) }
    var sharingMessage by remember(routeId) { mutableStateOf("Aguardando localização do aparelho…") }
    var sharingError by remember(routeId) { mutableStateOf(false) }
    var retrySharing by remember(routeId) { mutableIntStateOf(0) }

    LaunchedEffect(routeId, retrySharing, lifecycle) {
        sharingError = false
        if (!tracker.requestLocationPermission()) {
            sharingMessage = "Autorize a localização para aparecer no mapa."
            sharingError = true
            return@LaunchedEffect
        }
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            sharingMessage = "Aguardando localização do aparelho…"
            val sessionId = Uuid.random().toString()
            val updates = Channel<Location>(Channel.CONFLATED)
            try {
                val begun = CatalogRepository.beginMapSession(sessionId)
                if (begun is UiState.Error) {
                    sharingMessage = begun.message
                    sharingError = true
                    return@repeatOnLifecycle
                }
                tracker.startTracking { fix ->
                    currentLocation = fix
                    updates.trySend(fix)
                }
                for (fix in updates) {
                    val sent = CatalogRepository.publishMapPosition(sessionId, fix)
                    when (sent) {
                        is UiState.Success -> {
                            sharingMessage = "Sua rota aparece no mapa."
                            sharingError = false
                        }
                        is UiState.Error -> {
                            sharingMessage = sent.message
                            sharingError = true
                        }
                        UiState.Loading -> Unit
                    }
                    delay(5_000)
                }
            } finally {
                tracker.stopTracking()
                updates.close()
                withContext(NonCancellable) { CatalogRepository.endMapSession(sessionId) }
            }
        }
    }

    LaunchedEffect(routeId) {
        while (true) {
            try {
                clusters = engine.processIntents(routeId)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Previne crash silencioso
            }
            delay(30000) // Poll a cada 30s
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NativeMap(
            modifier = Modifier.fillMaxSize(),
            userLocation = currentLocation
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
                    sharingMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (sharingError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (sharingError) TextButton(onClick = { retrySharing++ }) { Text("Tentar novamente") }
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

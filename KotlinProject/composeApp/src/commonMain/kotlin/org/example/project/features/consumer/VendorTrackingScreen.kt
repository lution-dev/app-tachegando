package org.example.project.features.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.core.data.VendorRepository
import org.example.project.core.data.Vendor
import org.example.project.core.utils.UiState
import org.example.project.core.navigation.ArrowBackIcon
import org.example.project.core.navigation.LocationOnIcon
import org.example.project.core.navigation.PersonIcon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorTrackingScreen(
    vendorId: String,
    onBack: () -> Unit,
    onProfileClick: (String) -> Unit
) {
    val uiState by produceState<UiState<Vendor?>>(initialValue = UiState.Loading, vendorId) {
        val result = VendorRepository.getById(vendorId)
        value = UiState.Success(result)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acompanhar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = ArrowBackIcon, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Erro ao carregar os dados.")
                }
            }
            is UiState.Success -> {
                val vendor = (uiState as UiState.Success<Vendor?>).data
                val vendorsFlow = androidx.compose.runtime.remember { VendorRepository.listenToVendors() }
                val vendors by vendorsFlow.collectAsState(initial = emptyList())
                val currentVendor = vendors.find { it.id == vendorId } ?: vendor

                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = PersonIcon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            currentVendor?.name ?: "Profissional",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                val vendorLocation = currentVendor?.let {
                    if (it.latitude != null && it.longitude != null) org.example.project.core.utils.Location(it.latitude, it.longitude) else null
                }

                org.example.project.core.ui.maps.NativeMap(
                    modifier = Modifier.fillMaxSize(),
                    userLocation = null,
                    vendorLocations = vendorLocation?.let { listOf(it) } ?: emptyList(),
                    onMapClick = {}
                )
                // Overlay button
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    OutlinedButton(
                        onClick = { onProfileClick(vendorId) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text("Ver perfil")
                    }
                }
            }

            Text(
                text = "Está chegando em 2 minutos.",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Anuncie aqui", style = MaterialTheme.typography.titleMedium)
            }
        }
        }
        }
    }
}

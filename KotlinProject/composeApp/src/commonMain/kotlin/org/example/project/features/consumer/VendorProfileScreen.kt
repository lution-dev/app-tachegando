package org.example.project.features.consumer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.core.data.PurchaseIntentRepository
import org.example.project.core.data.VendorRepository
import org.example.project.core.data.Vendor
import org.example.project.core.utils.UiState
import org.example.project.core.utils.rememberLocationTracker
import org.example.project.core.navigation.ArrowBackIcon
import org.example.project.core.navigation.ArrowForwardIcon
import org.example.project.core.navigation.LocationOnIcon
import org.example.project.core.navigation.PersonIcon
import org.example.project.core.navigation.StarIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorProfileScreen(
    vendorId: String,
    onBack: () -> Unit,
    onTrackClick: (String) -> Unit
) {
    val uiState by produceState<UiState<Vendor?>>(initialValue = UiState.Loading, vendorId) {
        val result = VendorRepository.getById(vendorId)
        value = UiState.Success(result)
    }
    var isFollowing by remember { mutableStateOf(false) }
    var isAlertEnabled by remember { mutableStateOf(false) }
    var isAlertLoading by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }
    val locationTracker = rememberLocationTracker()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (uiState is UiState.Success) {
                        (uiState as UiState.Success<Vendor?>).data?.name ?: "Profissional"
                    } else "Carregando..."
                    Text(titleText)
                },
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
                    Text("Não deu para carregar os dados.")
                }
            }
            is UiState.Success -> {
                val vendor = (uiState as UiState.Success<Vendor?>).data
                if (vendor == null) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Profissional não encontrado", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = onBack) { Text("Voltar") }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding)
                    ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = PersonIcon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(vendor.name, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(vendor.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = StarIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(vendor.rating, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("(${vendor.reviewCount} avalia\u00e7\u00f5es)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = LocationOnIcon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${vendor.distance} de dist\u00e2ncia", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { onTrackClick(vendorId) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Acompanhar", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = ArrowForwardIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    isAlertLoading = true
                                    alertMessage = null
                                    val currentLocation = locationTracker.getCurrentLocation()
                                    if (currentLocation == null) {
                                        alertMessage = "Ative sua localização para receber o aviso."
                                        isAlertLoading = false
                                        return@launch
                                    }

                                    when (val result = PurchaseIntentRepository.createArrivalAlert(vendor.id, currentLocation)) {
                                        is UiState.Success -> {
                                            isAlertEnabled = true
                                            alertMessage = "Aviso ativado. Vamos te avisar quando estiver perto."
                                        }
                                        is UiState.Error -> alertMessage = result.message
                                        UiState.Loading -> {}
                                    }
                                    isAlertLoading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isAlertLoading && !isAlertEnabled
                        ) {
                            if (isAlertLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                            } else {
                                Text(if (isAlertEnabled) "Aviso ativado" else "Me avise quando chegar")
                            }
                        }
                        alertMessage?.let { message ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                message,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isAlertEnabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { isFollowing = !isFollowing },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isFollowing) "Parar de seguir" else "Seguir")
                        }
                    }
                }
                item {
                    Text(
                        "Produtos",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(vendor.products.size) { index ->
                    val product = vendor.products[index]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(product.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
        }
        }
    }
}

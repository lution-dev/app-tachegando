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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.core.data.VendorRepository
import org.example.project.core.navigation.ArrowBackIcon
import org.example.project.core.navigation.LocationOnIcon
import org.example.project.core.navigation.PersonIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorTrackingScreen(
    vendorId: String,
    onBack: () -> Unit,
    onProfileClick: (String) -> Unit
) {
    val vendor = VendorRepository.getById(vendorId)

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
                    vendor?.name ?: "Vendedor",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = LocationOnIcon, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mapa carregando rota", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = { onProfileClick(vendorId) }) {
                        Text("Ver Perfil do Vendedor")
                    }
                }
            }

            Text(
                text = "Aten\u00e7\u00e3o! Seu vendedor chega em 2 minutos.",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFEBEBEB)).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Anuncie aqui", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

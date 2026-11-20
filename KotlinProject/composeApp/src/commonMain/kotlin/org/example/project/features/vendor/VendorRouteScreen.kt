package org.example.project.features.vendor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.core.utils.Location
import org.example.project.core.utils.rememberLocationTracker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorRouteScreen(
    onStopRoute: () -> Unit,
    onStartRoute: () -> Unit = {}
) {
    val locationTracker = rememberLocationTracker()
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(locationTracker) {
        currentLocation = locationTracker.getCurrentLocation()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Minha rota") })
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Native Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                org.example.project.core.ui.maps.NativeMap(
                    modifier = Modifier.fillMaxSize(),
                    userLocation = currentLocation,
                    onMapClick = {}
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = onStartRoute,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("Criar nova rota")
                }

                Button(
                    onClick = onStopRoute,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Parar rota")
                }
            }
        }
    }
}

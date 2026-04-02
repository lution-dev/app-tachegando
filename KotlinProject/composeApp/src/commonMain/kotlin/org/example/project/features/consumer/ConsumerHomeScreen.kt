package org.example.project.features.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerHomeScreen(
    onVendorClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tá Chegando") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text("Mapa - Vendedores Próximos", style = MaterialTheme.typography.titleLarge)
                
                Button(
                    onClick = { onVendorClick("7") },
                    modifier = Modifier.padding(top = 64.dp)
                ) {
                    Text("Simular toque em Vendedor")
                }
            }
            
            // Search Bar Overlay
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surface),
                placeholder = { Text("Buscar vendedor ou produto...") }
            )
            
            // Ad Banner Placeholder at bottom (like in PDF)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Anuncie aqui", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

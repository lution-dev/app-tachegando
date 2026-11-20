package org.example.project.features.consumer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerProfileScreen(
    onFavoritesClick: () -> Unit,
    biometricSettings: (@Composable () -> Unit)?
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meu Perfil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Olá", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onFavoritesClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Favoritos")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (biometricSettings != null) {
                biometricSettings()
                Spacer(modifier = Modifier.height(16.dp))
            }

        }
    }
}

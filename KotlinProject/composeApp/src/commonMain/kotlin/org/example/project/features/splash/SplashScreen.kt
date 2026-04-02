package org.example.project.features.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L) // Simulate 2 seconds loading
        onNavigateNext()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tá Chegando",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

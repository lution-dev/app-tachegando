package org.example.project.features.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bem-vindo ao Tá Chegando!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ElevatedCard(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            BoxCenter {
                Text("Entrar", style = MaterialTheme.typography.titleLarge)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ElevatedCard(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            BoxCenter {
                Text("Criar nova conta", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun BoxCenter(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

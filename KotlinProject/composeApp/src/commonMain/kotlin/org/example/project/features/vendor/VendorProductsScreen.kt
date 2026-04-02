package org.example.project.features.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.core.data.Product
import org.example.project.core.navigation.ArrowBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorProductsScreen(
    onBack: () -> Unit
) {
    var products by remember {
        mutableStateOf(
            listOf(
                Product("p1", "Pão Francês (6 un)", "R$ 6,00"),
                Product("p2", "Pão de Queijo (10 un)", "R$ 12,00"),
                Product("p3", "Bolo de Chocolate", "R$ 35,00"),
                Product("p4", "Torta de Limão", "R$ 40,00"),
                Product("p5", "Sonho", "R$ 6,00")
            )
        )
    }
    var newProductName by remember { mutableStateOf("") }
    var newProductPrice by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Produtos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = ArrowBackIcon, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "${products.size} produtos cadastrados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(products) { index, product ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.SemiBold)
                            Text(product.price, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(
                            onClick = {
                                products = products.toMutableList().also { it.removeAt(index) }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Remover")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Adicionar Produto", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newProductName,
                onValueChange = { newProductName = it },
                label = { Text("Nome do produto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newProductPrice,
                onValueChange = { newProductPrice = it },
                label = { Text("Preço (ex: R$ 10,00)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (newProductName.isNotBlank() && newProductPrice.isNotBlank()) {
                        products = products + Product("new_${products.size}", newProductName, newProductPrice)
                        newProductName = ""
                        newProductPrice = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = newProductName.isNotBlank() && newProductPrice.isNotBlank()
            ) {
                Text("Adicionar Produto")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar e Voltar")
            }
        }
    }
}

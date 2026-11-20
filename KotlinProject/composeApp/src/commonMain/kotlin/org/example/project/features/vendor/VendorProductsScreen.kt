package org.example.project.features.vendor


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import org.example.project.core.data.CatalogProduct
import org.example.project.core.data.CatalogRepository
import org.example.project.core.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VendorProductsScreen(userId: String, catalogRevision: Int, onEditProduct: (String?) -> Unit) {
    val scope = rememberCoroutineScope()
    var reload by remember { mutableStateOf(0) }
    var removing by remember { mutableStateOf<CatalogProduct?>(null) }
    var busy by remember { mutableStateOf(false) }
    var removeError by remember { mutableStateOf<String?>(null) }
    val catalog by produceState<UiState<List<CatalogProduct>>>(UiState.Loading, userId, reload, catalogRevision) {
        value = CatalogRepository.products()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Meus produtos") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (val result = catalog) {
                UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Error -> item {
                    Text(result.message, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { reload++ }) { Text("Tentar novamente") }
                }
                is UiState.Success -> {
                    item {
                        Button(
                            onClick = { onEditProduct(null) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Adicionar produto") }
                    }
                    if (result.data.isEmpty()) item { Text("Seu catálogo está vazio.") }
                    items(result.data, key = { it.id }) { product ->
                        val photo by produceState<ByteArray?>(null, product, reload, catalogRevision) {
                            value = if (product.hasPhoto) CatalogRepository.productPhoto(product.id) else null
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PhotoPreview(photo, null, Modifier.size(56.dp))
                            Text(product.name, modifier = Modifier.weight(1f))
                            TextButton(onClick = { onEditProduct(product.id) }) { Text("Editar") }
                            TextButton(onClick = { removeError = null; removing = product }) { Text("Remover") }
                        }
                    }
                }
            }
        }
    }

    removing?.let { product ->
        AlertDialog(
            onDismissRequest = { if (!busy) removing = null },
            title = { Text("Remover produto?") },
            text = {
                Column {
                    Text("“${product.name}” sairá do seu catálogo.")
                    removeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            dismissButton = { TextButton(onClick = { removing = null }, enabled = !busy) { Text("Cancelar") } },
            confirmButton = {
                TextButton(enabled = !busy, onClick = {
                    busy = true
                    scope.launch {
                        try {
                            when (val result = CatalogRepository.removeProduct(product.id)) {
                                is UiState.Success -> { removing = null; reload++ }
                                is UiState.Error -> removeError = result.message
                                UiState.Loading -> Unit
                            }
                        } finally {
                            busy = false
                        }
                    }
                }) { Text("Remover") }
            }
        )
    }
}

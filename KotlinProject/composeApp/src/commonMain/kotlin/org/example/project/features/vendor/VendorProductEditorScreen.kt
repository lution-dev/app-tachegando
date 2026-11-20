package org.example.project.features.vendor

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

import org.example.project.core.data.CatalogProduct
import org.example.project.core.data.CatalogRepository
import org.example.project.core.data.PhotoChange
import org.example.project.core.navigation.ArrowBackIcon
import org.example.project.core.utils.UiState

private class ProductDraft : ViewModel() {
    var photo by mutableStateOf<PhotoChange?>(null)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
internal fun VendorProductEditorScreen(productId: String?, onBack: () -> Unit, onSaved: () -> Unit) {
    val newProductId = rememberSaveable(productId) { productId ?: Uuid.random().toString() }
    val draft: ProductDraft = viewModel { ProductDraft() }
    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }
    var importingPhoto by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var retry by remember { mutableStateOf(0) }
    val productState by produceState<UiState<CatalogProduct>>(UiState.Loading, productId, retry) {
        value = if (productId == null) {
            UiState.Success(CatalogProduct(newProductId, ""))
        } else {
            when (val result = CatalogRepository.products()) {
                is UiState.Success -> result.data.find { it.id == productId }?.let { UiState.Success(it) }
                    ?: UiState.Error("Este produto não está mais disponível.")
                is UiState.Error -> result
                UiState.Loading -> UiState.Loading
            }
        }
    }
    val busy = saving || importingPhoto

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "Novo produto" else "Editar produto") },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !busy) {
                        Icon(ArrowBackIcon, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = productState) {
                UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { retry++ }) { Text("Tentar novamente") }
                }
                is UiState.Success -> {
                    val product = state.data
                    var name by rememberSaveable(product.id) { mutableStateOf(product.name) }
                    val current by produceState<ByteArray?>(null, product.id, product.hasPhoto) {
                        value = if (product.hasPhoto) CatalogRepository.productPhoto(product.id) else null
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 160) name = it },
                        label = { Text("Nome do produto") },
                        singleLine = true,
                        enabled = !busy,
                        modifier = Modifier.fillMaxWidth()
                    )
                    PhotoEditor(
                        label = "Foto do produto (opcional)",
                        current = current,
                        change = draft.photo,
                        busy = saving,
                        allowCamera = true,
                        onPhotoBusy = { importingPhoto = it },
                        onChange = { draft.photo = it }
                    )
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        enabled = !busy && name.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            saving = true
                            error = null
                            scope.launch {
                                try {
                                    when (val result = CatalogRepository.saveProduct(product.id, name.trim(), draft.photo)) {
                                        is UiState.Success -> onSaved()
                                        is UiState.Error -> error = result.message
                                        UiState.Loading -> Unit
                                    }
                                } finally {
                                    saving = false
                                }
                            }
                        }
                    ) { Text(if (saving) "Salvando…" else "Salvar") }
                    TextButton(onClick = onBack, enabled = !busy) { Text("Cancelar") }
                }
            }
        }
    }
}

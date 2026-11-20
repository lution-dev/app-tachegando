package org.example.project.core.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import org.example.project.core.data.CatalogRepository
import org.example.project.core.data.VendorMapMarker
import org.example.project.core.utils.UiState

@Composable
internal fun rememberActiveVendorMarkers(vendorId: String? = null): State<List<VendorMapMarker>> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return produceState(emptyList(), vendorId, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                value = (CatalogRepository.mapMarkers(vendorId) as? UiState.Success)?.data.orEmpty()
                delay(8_000)
            }
        }
    }
}

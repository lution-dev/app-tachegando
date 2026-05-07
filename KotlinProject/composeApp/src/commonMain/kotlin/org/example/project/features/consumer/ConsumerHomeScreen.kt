package org.example.project.features.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import org.example.project.core.data.VendorRepository
import org.example.project.core.data.Vendor
import org.example.project.core.utils.UiState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import org.example.project.core.navigation.PersonIcon
import org.example.project.core.navigation.StarIcon
import org.example.project.core.navigation.LocationOnIcon
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.runtime.collectAsState
import org.example.project.core.utils.Location
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerHomeScreen(
    onVendorClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var sheetState by remember { mutableStateOf(ConsumerMapSheetState()) }
    var expandedSheetDragOffset by remember { mutableStateOf(0f) }
    var collapsedSheetDragOffset by remember { mutableStateOf(0f) }

    val vendorsFlow = remember { VendorRepository.listenToVendors() }
    val vendors by vendorsFlow.collectAsState(initial = emptyList())
    val uiState = if (vendors.isNotEmpty()) UiState.Success(vendors) else UiState.Loading

    val vendorLocations = vendors.mapNotNull {
        if (it.latitude != null && it.longitude != null) Location(it.latitude, it.longitude) else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tá Chegando") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Native Map KMP Component
            org.example.project.core.ui.maps.NativeMap(
                modifier = Modifier.fillMaxSize(),
                userLocation = null, // TODO: Add consumer's own location tracking later
                vendorLocations = vendorLocations,
                onMapClick = { }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                if (sheetState.isVisible) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(0, expandedSheetDragOffset.roundToInt()) },
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
                                .padding(top = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.pointerInput(Unit) {
                                    detectVerticalDragGestures(
                                        onDragCancel = { expandedSheetDragOffset = 0f },
                                        onDragEnd = {
                                            sheetState = sheetState.settleExpandedDrag(expandedSheetDragOffset)
                                            expandedSheetDragOffset = 0f
                                        },
                                        onVerticalDrag = { change, dragAmount ->
                                            change.consume()
                                            expandedSheetDragOffset = (expandedSheetDragOffset + dragAmount)
                                                .coerceIn(0f, 220f)
                                        }
                                    )
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .width(44.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Profissionais perto",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    TextButton(onClick = { sheetState = sheetState.hide() }) {
                                        Text("Esconder lista")
                                    }
                                }
                            }

                            when (uiState) {
                                is UiState.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(180.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                is UiState.Error -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(120.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Não deu para carregar.")
                                    }
                                }
                                is UiState.Success -> {
                                    val vendors = uiState.data
                                    val filtered = if (searchQuery.isBlank()) vendors else vendors.filter {
                                        it.name.contains(searchQuery, ignoreCase = true) ||
                                        it.description.contains(searchQuery, ignoreCase = true) ||
                                        it.category.contains(searchQuery, ignoreCase = true)
                                    }
                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth().height(250.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 4.dp,
                                            bottom = 12.dp
                                        ),
                                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(filtered) { vendor ->
                                            Card(
                                                onClick = { onVendorClick(vendor.id) },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(imageVector = PersonIcon, contentDescription = null, modifier = Modifier.size(24.dp))
                                                    }
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(vendor.name, fontWeight = FontWeight.Bold)
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(imageVector = StarIcon, contentDescription = null, modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(2.dp))
                                                            Text(vendor.rating, style = MaterialTheme.typography.bodySmall)
                                                            Spacer(modifier = Modifier.width(12.dp))
                                                            Icon(imageVector = LocationOnIcon, contentDescription = null, modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(2.dp))
                                                            Text(vendor.distance, style = MaterialTheme.typography.bodySmall)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        onClick = { sheetState = sheetState.show() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .offset { IntOffset(0, collapsedSheetDragOffset.roundToInt()) }
                            .pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onDragCancel = { collapsedSheetDragOffset = 0f },
                                    onDragEnd = {
                                        sheetState = sheetState.settleCollapsedDrag(collapsedSheetDragOffset)
                                        collapsedSheetDragOffset = 0f
                                    },
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        collapsedSheetDragOffset = (collapsedSheetDragOffset + dragAmount)
                                            .coerceIn(-160f, 0f)
                                    }
                                )
                            },
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                            )
                            Text(
                                "Ver profissionais perto",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Anuncie aqui", style = MaterialTheme.typography.titleMedium)
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
                placeholder = { Text("Buscar profissional ou produto...") }
            )
        }
    }
}

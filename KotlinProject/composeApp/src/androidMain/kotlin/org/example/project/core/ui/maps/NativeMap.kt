package org.example.project.core.ui.maps

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import org.example.project.core.data.CatalogRepository
import org.example.project.core.data.VendorMapMarker
import org.example.project.core.utils.Location

import org.example.project.core.ui.maps.HeatZoneLocation

@Composable
actual fun NativeMap(
    modifier: Modifier,
    userLocation: Location?,
    vendorLocations: List<Location>,
    routePoints: List<Location>,
    heatZones: List<HeatZoneLocation>,
    onMapClick: (Location) -> Unit,
    activeVendors: List<VendorMapMarker>,
    onVendorClick: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val mapAvailable = remember(context) {
        try {
            MapsInitializer.initialize(context) == ConnectionResult.SUCCESS
        } catch (_: RuntimeException) {
            false
        }
    }
    if (!mapAvailable) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(
                "O Google Maps está indisponível neste aparelho. Verifique o Google Play Services.",
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val markerSize = with(LocalDensity.current) { 56.dp.roundToPx() }
    var centeredOnVendor by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(-23.550520, -46.633308), // SP default
            if (userLocation != null) 15f else 10f
        )
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
        }
    }

    val firstVendor = activeVendors.firstOrNull()
    LaunchedEffect(firstVendor?.vendorId, userLocation) {
        if (!centeredOnVendor && userLocation == null && firstVendor != null) {
            centeredOnVendor = true
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstVendor.latitude, firstVendor.longitude), 14f
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false),
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        onMapClick = { latLng -> onMapClick(Location(latLng.latitude, latLng.longitude)) }
    ) {
        userLocation?.let {
            Marker(
                state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                title = "Você"
            )
        }
        vendorLocations.forEach { loc ->
            Marker(
                state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                title = "Profissional"
            )
        }
        activeVendors.forEach { vendor ->
            key(vendor.vendorId) {
                val position = LatLng(vendor.latitude, vendor.longitude)
                val state = remember { MarkerState(position) }
                SideEffect { state.position = position }
                val avatar by produceState<Bitmap?>(null, vendor.hasPhoto, vendor.avatarRevision) {
                    value = if (vendor.hasPhoto) CatalogRepository.avatar(vendor.vendorId)?.let { bytes ->
                        withContext(Dispatchers.Default) { decodeMarkerAvatar(bytes) }
                    } else null
                }
                val transport = vendor.transport
                val markerIcon = remember(avatar, transport, markerSize) {
                    if (avatar == null && transport == null) null
                    else BitmapDescriptorFactory.fromBitmap(renderVendorMarker(avatar, transport, markerSize))
                }
                Marker(
                    state = state,
                    icon = markerIcon,
                    title = vendor.name,
                    snippet = transport?.label,
                    onClick = {
                        onVendorClick(vendor.vendorId)
                        false
                    }
                )
            }
        }
        if (routePoints.size >= 2) {
            Polyline(
                points = routePoints.map { LatLng(it.latitude, it.longitude) },
                color = androidx.compose.ui.graphics.Color(0xFFFF6B00),
                width = 8f
            )
        }
        routePoints.forEachIndexed { index, loc ->
            val isClosedLoop = routePoints.size >= 3 && routePoints.first() == routePoints.last()
            Marker(
                state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                title = when {
                    index == 0 -> "Início (0)"
                    isClosedLoop && index == routePoints.lastIndex -> "Volta ao início"
                    else -> "Ponto $index"
                }
            )
        }
        heatZones.forEach { zone ->
            com.google.maps.android.compose.Circle(
                center = LatLng(zone.location.latitude, zone.location.longitude),
                radius = zone.radius,
                fillColor = androidx.compose.ui.graphics.Color.Red.copy(alpha = zone.intensity * 0.5f),
                strokeColor = androidx.compose.ui.graphics.Color.Transparent,
                strokeWidth = 0f
            )
        }
    }
}

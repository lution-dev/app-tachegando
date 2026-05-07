package org.example.project.core.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import org.example.project.core.utils.Location

import org.example.project.core.ui.maps.HeatZoneLocation

@Composable
actual fun NativeMap(
    modifier: Modifier,
    userLocation: Location?,
    vendorLocations: List<Location>,
    routePoints: List<Location>,
    heatZones: List<HeatZoneLocation>,
    onMapClick: (Location) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(-23.550520, -46.633308), // SP default
            if (userLocation != null) 15f else 10f
        )
    }

    androidx.compose.runtime.LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude), 15f
                )
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

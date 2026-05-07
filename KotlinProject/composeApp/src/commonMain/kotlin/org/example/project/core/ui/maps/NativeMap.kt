package org.example.project.core.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.core.utils.Location

data class HeatZoneLocation(
    val location: Location,
    val intensity: Float, // 0.0 to 1.0
    val radius: Double = 1000.0 // meters
)

@Composable
expect fun NativeMap(
    modifier: Modifier = Modifier,
    userLocation: Location? = null,
    vendorLocations: List<Location> = emptyList(),
    routePoints: List<Location> = emptyList(),
    heatZones: List<HeatZoneLocation> = emptyList(),
    onMapClick: (Location) -> Unit = {}
)

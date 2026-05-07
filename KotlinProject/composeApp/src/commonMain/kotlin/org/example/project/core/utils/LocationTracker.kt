package org.example.project.core.utils

import androidx.compose.runtime.Composable

interface LocationTracker {
    suspend fun requestLocationPermission(): Boolean = true
    suspend fun getCurrentLocation(): Location?
    fun startTracking(onLocationUpdate: (Location) -> Unit)
    fun stopTracking()
}

@Composable
expect fun rememberLocationTracker(): LocationTracker

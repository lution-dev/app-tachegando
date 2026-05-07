package org.example.project.core.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private fun Context.hasLocationPermission(): Boolean =
    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

class AndroidLocationTracker(
    private val context: Context,
    private val permissionRequester: suspend () -> Boolean
) : LocationTracker {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    override suspend fun requestLocationPermission(): Boolean {
        if (context.hasLocationPermission()) return true
        return permissionRequester()
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        if (!context.hasLocationPermission()) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: android.location.Location? ->
                if (loc != null) {
                    cont.resume(Location(loc.latitude, loc.longitude))
                } else {
                    cont.resume(null)
                }
            }.addOnFailureListener {
                cont.resume(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (cont.isActive) cont.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    override fun startTracking(onLocationUpdate: (Location) -> Unit) {
        if (!context.hasLocationPermission()) return
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    onLocationUpdate(Location(it.latitude, it.longitude))
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            locationCallback = null
        }
    }

    override fun stopTracking() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }
}

@Composable
actual fun rememberLocationTracker(): LocationTracker {
    val context = LocalContext.current
    val pendingPermission = remember { mutableStateOf<CancellableContinuation<Boolean>?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        pendingPermission.value?.let { cont ->
            if (cont.isActive) cont.resume(granted)
        }
        pendingPermission.value = null
    }
    return remember(context, permissionLauncher) {
        AndroidLocationTracker(context) {
            if (context.hasLocationPermission()) {
                true
            } else {
                suspendCancellableCoroutine { cont ->
                    pendingPermission.value?.cancel()
                    pendingPermission.value = cont
                    cont.invokeOnCancellation {
                        if (pendingPermission.value === cont) {
                            pendingPermission.value = null
                        }
                    }
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }
}

package org.example.project.features.vendor.err

import org.example.project.core.domain.models.RouteWaypoint
import org.example.project.core.utils.GeoUtils
import org.example.project.core.utils.Location

data class RouteDraftState(
    val waypoints: List<RouteWaypoint> = emptyList(),
    val creationMode: RouteCreationMode = RouteCreationMode.Recording,
    val isRecording: Boolean = false
) {
    val isClosed: Boolean
        get() = waypoints.size >= MIN_LOOP_POINTS && waypoints.first().samePlaceAs(waypoints.last())

    val canCloseRoute: Boolean
        get() = waypoints.size >= MIN_LOOP_POINTS && !isClosed

    val canStartRoute: Boolean
        get() = waypoints.size >= MIN_ROUTE_POINTS

    val routePoints: List<Location>
        get() = waypoints.map { Location(it.latitude, it.longitude) }

    val guidanceText: String
        get() = when (creationMode) {
            RouteCreationMode.Recording -> when {
                isRecording && waypoints.isEmpty() -> "Aguardando GPS..."
                isRecording -> "${waypoints.size} pontos gravados. Continue seu caminho."
                waypoints.isEmpty() -> "Aperte gravar e faca seu caminho normal."
                canStartRoute -> "Rota gravada. Revise e salve."
                else -> "Grave pelo menos dois pontos para salvar."
            }
            RouteCreationMode.Manual -> when (waypoints.size) {
                0 -> "Toque no mapa para marcar o inicio."
                1 -> "Inicio marcado. Toque na proxima esquina."
                else -> if (isClosed) {
                    "Volta fechada com ${waypoints.size - 1} pontos."
                } else {
                    "${waypoints.size} pontos marcados. Continue tocando nas esquinas."
                }
            }
        }

    fun switchToRecording(): RouteDraftState {
        return copy(
            waypoints = emptyList(),
            creationMode = RouteCreationMode.Recording,
            isRecording = false
        )
    }

    fun switchToManual(): RouteDraftState {
        return copy(
            waypoints = emptyList(),
            creationMode = RouteCreationMode.Manual,
            isRecording = false
        )
    }

    fun startRecording(initialLocation: Location?): RouteDraftState {
        val started = copy(
            waypoints = emptyList(),
            creationMode = RouteCreationMode.Recording,
            isRecording = true
        )
        return if (initialLocation == null) started else started.appendPoint(initialLocation)
    }

    fun recordLocation(location: Location): RouteDraftState {
        if (creationMode != RouteCreationMode.Recording || !isRecording) return this
        val lastPoint = routePoints.lastOrNull()
        if (lastPoint != null && GeoUtils.calculateDistanceMeters(lastPoint, location) < MIN_RECORDED_POINT_DISTANCE_METERS) {
            return this
        }
        return appendPoint(location)
    }

    fun finishRecording(): RouteDraftState {
        if (creationMode != RouteCreationMode.Recording) return this
        return copy(isRecording = false)
    }

    fun addPoint(location: Location): RouteDraftState {
        return appendPoint(location)
    }

    fun removeLastPoint(): RouteDraftState {
        return copy(waypoints = waypoints.dropLast(1).reindex())
    }

    fun closeLoop(): RouteDraftState {
        if (!canCloseRoute) return this
        val start = waypoints.first()
        return copy(
            waypoints = waypoints + start.copy(orderIndex = waypoints.size)
        )
    }

    fun clear(): RouteDraftState {
        return copy(waypoints = emptyList(), isRecording = false)
    }

    private fun appendPoint(location: Location): RouteDraftState {
        return copy(
            waypoints = waypoints + RouteWaypoint(
                latitude = location.latitude,
                longitude = location.longitude,
                orderIndex = waypoints.size
            )
        )
    }

    private fun List<RouteWaypoint>.reindex(): List<RouteWaypoint> {
        return mapIndexed { index, waypoint -> waypoint.copy(orderIndex = index) }
    }

    private fun RouteWaypoint.samePlaceAs(other: RouteWaypoint): Boolean {
        return latitude == other.latitude && longitude == other.longitude
    }

    private companion object {
        const val MIN_ROUTE_POINTS = 2
        const val MIN_LOOP_POINTS = 3
        const val MIN_RECORDED_POINT_DISTANCE_METERS = 10.0
    }
}

enum class RouteCreationMode {
    Recording,
    Manual
}

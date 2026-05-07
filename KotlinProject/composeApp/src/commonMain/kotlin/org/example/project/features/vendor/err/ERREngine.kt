package org.example.project.features.vendor.err

import org.example.project.core.data.RouteRepository
import org.example.project.core.domain.models.*
import org.example.project.core.utils.ClusterEngine

class ERREngine(
    private val repository: RouteRepository,
    private val config: ERRConfig = ERRConfig()
) {
    private val clusterEngine = ClusterEngine(config)

    var currentRoute: VendorRoute? = null
        private set

    suspend fun initRoute(
        vendorId: String,
        waypoints: List<RouteWaypoint>,
        routeName: String = "Rota sem nome",
        activeDays: List<RouteWeekday> = RouteWeekday.everyDay()
    ): VendorRoute {
        if (waypoints.isEmpty()) throw IllegalArgumentException("Waypoints cannot be empty")
        if (activeDays.isEmpty()) throw IllegalArgumentException("Route must have at least one active day")

        val polygon = repository.generateBufferPolygon(waypoints, config.deviationToleranceMeters)
        val route = VendorRoute(
            id = "temp_${System.currentTimeMillis()}",
            vendorId = vendorId,
            name = routeName,
            waypoints = waypoints,
            influencePolygon = polygon,
            deviationToleranceMeters = config.deviationToleranceMeters,
            activeDays = activeDays,
            isActive = true
        )

        val routeId = repository.saveRoute(route)
        val activeRoute = route.copy(id = routeId)
        currentRoute = activeRoute
        return activeRoute
    }

    suspend fun loadRoute(routeId: String): VendorRoute? {
        currentRoute = repository.getRoute(routeId)
        return currentRoute
    }

    suspend fun processIntents(routeId: String? = null): List<DemandCluster> {
        val route = currentRoute ?: routeId?.let { loadRoute(it) } ?: return emptyList()
        val intents = repository.fetchIntentsInPolygon(route.influencePolygon)
        return clusterEngine.generateClusters(intents, route.waypoints)
    }
}

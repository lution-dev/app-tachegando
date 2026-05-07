package org.example.project.core.data

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.example.project.core.domain.models.PurchaseIntent
import org.example.project.core.domain.models.RouteWeekday
import org.example.project.core.domain.models.RouteWaypoint
import org.example.project.core.domain.models.VendorRoute
import org.example.project.core.utils.Location

class RouteRepository {
    @Serializable
    private data class VendorRouteInsert(
        @SerialName("vendor_id") val vendorId: String,
        val name: String,
        val waypoints: JsonArray,
        @SerialName("deviation_tolerance_meters") val deviationToleranceMeters: Int,
        @SerialName("influence_polygon") val influencePolygon: String,
        @SerialName("active_days") val activeDays: JsonArray,
        @SerialName("is_active") val isActive: Boolean
    )

    @Serializable
    private data class SavedRouteId(val id: String)

    @Serializable
    private data class IntentRpcRow(
        val id: String,
        @SerialName("consumer_id") val consumerId: String,
        val lat: Double,
        val lng: Double,
        val status: String
    )

    suspend fun generateBufferPolygon(
        waypoints: List<RouteWaypoint>,
        toleranceMeters: Int
    ): List<Location> {
        runCatching {
            val params = buildJsonObject {
                put("points", routePointsJson(waypoints))
                put("tolerance_meters", JsonPrimitive(toleranceMeters))
            }
            val result = supabase.postgrest.rpc("create_route_buffer", params)
            parsePolygonCoordinates(Json.parseToJsonElement(result.data)).takeIf { it.isNotEmpty() }
        }.getOrNull()?.let { return it }

        val margin = toleranceMeters / 111000.0
        val minLat = waypoints.minOf { it.latitude } - margin
        val maxLat = waypoints.maxOf { it.latitude } + margin
        val minLng = waypoints.minOf { it.longitude } - margin
        val maxLng = waypoints.maxOf { it.longitude } + margin

        return listOf(
            Location(minLat, minLng),
            Location(minLat, maxLng),
            Location(maxLat, maxLng),
            Location(maxLat, minLng),
            Location(minLat, minLng)
        )
    }

    suspend fun saveRoute(route: VendorRoute): String {
        return try {
            val saved = supabase.postgrest["vendor_routes"].insert(
                VendorRouteInsert(
                    vendorId = route.vendorId,
                    name = route.name,
                    waypoints = waypointsJson(route.waypoints),
                    deviationToleranceMeters = route.deviationToleranceMeters,
                    influencePolygon = polygonWkt(route.influencePolygon),
                    activeDays = activeDaysJson(route.activeDays),
                    isActive = route.isActive
                )
            ) {
                select()
            }.decodeSingle<SavedRouteId>()
            val persistedRoute = route.copy(id = saved.id)
            activeRoutes[saved.id] = persistedRoute
            saved.id
        } catch (e: Exception) {
            e.printStackTrace()
            activeRoutes[route.id] = route
            route.id
        }
    }

    suspend fun getRoute(routeId: String): VendorRoute? {
        return activeRoutes[routeId]
    }

    suspend fun fetchIntentsInPolygon(polygon: List<Location>): List<PurchaseIntent> {
        return try {
            val params = buildJsonObject {
                put("coordinates", polygonCoordinatesJson(polygon))
            }
            supabase.postgrest.rpc("get_intents_in_polygon", params)
                .decodeList<IntentRpcRow>()
                .map { row ->
                    PurchaseIntent(
                        id = row.id,
                        consumerId = row.consumerId,
                        location = Location(row.lat, row.lng),
                        status = row.status
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun routePointsJson(waypoints: List<RouteWaypoint>): JsonArray {
        return buildJsonArray {
            waypoints.sortedBy { it.orderIndex }.forEach { waypoint ->
                add(
                    buildJsonArray {
                        add(JsonPrimitive(waypoint.longitude))
                        add(JsonPrimitive(waypoint.latitude))
                    }
                )
            }
        }
    }

    private fun waypointsJson(waypoints: List<RouteWaypoint>): JsonArray {
        return buildJsonArray {
            waypoints.sortedBy { it.orderIndex }.forEach { waypoint ->
                add(
                    buildJsonObject {
                        put("latitude", JsonPrimitive(waypoint.latitude))
                        put("longitude", JsonPrimitive(waypoint.longitude))
                        put("orderIndex", JsonPrimitive(waypoint.orderIndex))
                    }
                )
            }
        }
    }

    private fun activeDaysJson(activeDays: List<RouteWeekday>): JsonArray {
        return buildJsonArray {
            activeDays.sortedBy { it.ordinal }.forEach { day ->
                add(JsonPrimitive(day.storageValue))
            }
        }
    }

    private fun polygonCoordinatesJson(polygon: List<Location>): JsonArray {
        return buildJsonArray {
            closedPolygon(polygon).forEach { point ->
                add(
                    buildJsonArray {
                        add(JsonPrimitive(point.longitude))
                        add(JsonPrimitive(point.latitude))
                    }
                )
            }
        }
    }

    private fun polygonWkt(polygon: List<Location>): String {
        val coordinates = closedPolygon(polygon).joinToString(",") { point ->
            "${point.longitude} ${point.latitude}"
        }
        return "POLYGON(($coordinates))"
    }

    private fun closedPolygon(polygon: List<Location>): List<Location> {
        if (polygon.isEmpty()) return emptyList()
        return if (polygon.first() == polygon.last()) polygon else polygon + polygon.first()
    }

    private fun parsePolygonCoordinates(json: kotlinx.serialization.json.JsonElement): List<Location> {
        val rings = json.jsonArray
        val firstRing = rings.firstOrNull()?.jsonArray ?: return emptyList()
        return firstRing.mapNotNull { coordinate ->
            val pair = coordinate.jsonArray
            val longitude = pair.getOrNull(0)?.jsonPrimitive?.doubleOrNull
            val latitude = pair.getOrNull(1)?.jsonPrimitive?.doubleOrNull
            if (latitude != null && longitude != null) Location(latitude, longitude) else null
        }
    }

    private companion object {
        private val activeRoutes = mutableMapOf<String, VendorRoute>()
    }
}

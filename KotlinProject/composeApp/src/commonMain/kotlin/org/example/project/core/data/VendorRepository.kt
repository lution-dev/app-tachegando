package org.example.project.core.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vendor(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val distance: String,
    val rating: String,
    @SerialName("review_count") val reviewCount: Int,
    @SerialName("is_online") val isOnline: Boolean,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val products: List<Product> = emptyList()
)

@Serializable
data class Product(
    val id: String = "",
    val name: String,
    val price: String = ""
)

object VendorRepository {
    val vendors = emptyList<Vendor>()

    suspend fun getVendors(): List<Vendor> {
        return try {
            supabase.postgrest["vendors"].select().decodeList<Vendor>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateLocation(vendorId: String, latitude: Double, longitude: Double) {
        try {
            supabase.postgrest["vendors"].update(
                {
                    set("latitude", latitude)
                    set("longitude", longitude)
                }
            ) {
                filter { eq("id", vendorId) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateLocationForUser(userId: String, latitude: Double, longitude: Double) {
        try {
            supabase.postgrest["vendors"].update(
                {
                    set("latitude", latitude)
                    set("longitude", longitude)
                }
            ) {
                filter { eq("user_id", userId) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            updateLocation(userId, latitude, longitude)
        }
    }

    // Subscribe to real-time changes
    fun listenToVendors(): Flow<List<Vendor>> {
        return flow {
            try {
                // Initial load
                val initial = getVendors()
                emit(initial)

                // Then subscribe
                val channel = supabase.channel("public:vendors")
                val changes = channel.postgresChangeFlow<PostgresAction.Update>("public") {
                    table = "vendors"
                }
                channel.subscribe()

                changes.collect { action ->
                    // Re-fetch all to keep simple for now (can be optimized to update just one item)
                    val updated = getVendors()
                    emit(updated)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getById(id: String): Vendor? {
        return try {
            supabase.postgrest["vendors"].select {
                filter { eq("id", id) }
            }.decodeSingleOrNull<Vendor>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getByUserId(userId: String): Vendor? {
        return try {
            supabase.postgrest["vendors"].select {
                filter { eq("user_id", userId) }
            }.decodeSingleOrNull<Vendor>()
        } catch (e: Exception) {
            e.printStackTrace()
            getById(userId)
        }
    }

    suspend fun resolveRouteVendorId(userId: String): String {
        return getByUserId(userId)?.id ?: userId
    }

    suspend fun getByCategory(category: String): List<Vendor> {
        return try {
            supabase.postgrest["vendors"].select {
                filter { eq("category", category) }
            }.decodeList<Vendor>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    val favorites = listOf("1", "4", "12", "16")

    val notifications = listOf(
        "Tapioca da Tia" to "está a 0.1 km de você!",
        "Pão Quentinho" to "acabou de ficar online!",
        "Doces da Dona Lúcia" to "tem um novo produto!",
        "Banca do Seu João" to "está chegando em 3 min!",
        "Açaí da Amazônia" to "promoção: Açaí 500ml com oferta especial!",
        "Dogão do Manoel" to "está a 0.4 km de você!",
        "Dona Maria - Ovos Orgânicos" to "voltou a vender!",
        "Pão Quentinho" to "pão fresco saindo do forno!"
    )
}

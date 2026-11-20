package org.example.project.core.data

import kotlin.io.encoding.Base64

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

import org.example.project.core.utils.Location
import org.example.project.core.utils.UiState

@Serializable
enum class VendorTransport(val label: String, val symbol: String) {
    @SerialName("walking") Walking("A pé", "🚶"),
    @SerialName("bicycle") Bicycle("Bicicleta", "🚲"),
    @SerialName("motorcycle") Motorcycle("Moto", "🏍️"),
    @SerialName("car") Car("Carro", "🚗"),
    @SerialName("van") Van("Van", "🚐"),
    @SerialName("cart") Cart("Carrinho", "🛒");

    val storageName get() = name.lowercase()
}

@Serializable
internal data class VendorMapProfile(
    val transport: VendorTransport? = null,
    val avatar: String? = null
)

@Serializable
internal data class CatalogProduct(
    val id: String,
    val name: String,
    val price: String? = null,
    @SerialName("has_photo") val hasPhoto: Boolean = false
)

@Serializable
data class VendorMapMarker(
    @SerialName("vendor_id") val vendorId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val transport: VendorTransport? = null,
    @SerialName("has_photo") val hasPhoto: Boolean = false,
    @SerialName("avatar_revision") val avatarRevision: String? = null
) {
    val location get() = Location(latitude, longitude)
}

internal const val MAX_PHOTO_BYTES = 192 * 1024

internal class PhotoChange(val bytes: ByteArray?)

internal fun String.toPhotoBytes(): ByteArray? = runCatching {
    Base64.decode(filterNot(Char::isWhitespace)).takeIf { it.size in 4..MAX_PHOTO_BYTES }
}.getOrNull()

internal object CatalogRepository {
    private val api get() = supabase.postgrest

    suspend fun profile(): UiState<VendorMapProfile> = request("Não foi possível carregar seu perfil de vendedor.") {
        val result = api.rpc("app_get_vendor_profile")
        if (result.data.trim() == "null") {
            return UiState.Error("Finalize seu cadastro de vendedor para personalizar o perfil.")
        }
        result.decodeAs<VendorMapProfile>()
    }

    suspend fun saveProfile(transport: VendorTransport?, photo: PhotoChange?): UiState<Unit> =
        request("Não foi possível salvar o perfil. Tente novamente.") {
            api.rpc("app_save_vendor_profile", buildJsonObject {
                put("p_transport", transport?.storageName?.let(::JsonPrimitive) ?: JsonNull)
                put("p_photo_changed", photo != null)
                put("p_photo", photo?.bytes?.let { JsonPrimitive(Base64.encode(it)) } ?: JsonNull)
            })
            Unit
        }

    suspend fun products(vendorId: String? = null): UiState<List<CatalogProduct>> =
        request("Não foi possível carregar os produtos. Tente novamente.") {
            api.rpc("app_get_catalog", buildJsonObject {
                put("p_vendor_id", vendorId?.let(::JsonPrimitive) ?: JsonNull)
            }).decodeList<CatalogProduct>()
        }

    suspend fun productPhoto(id: String): ByteArray? =
        photo("app_get_product_photo", "p_product_id", id)

    suspend fun avatar(vendorId: String): ByteArray? =
        photo("app_get_map_avatar", "p_vendor_id", vendorId)

    suspend fun saveProduct(id: String, name: String, photo: PhotoChange?): UiState<Unit> =
        request("Não foi possível salvar o produto. Tente novamente.") {
            api.rpc("app_save_product", buildJsonObject {
                put("p_id", id)
                put("p_name", name)
                put("p_photo_changed", photo != null)
                put("p_photo", photo?.bytes?.let { JsonPrimitive(Base64.encode(it)) } ?: JsonNull)
            })
            Unit
        }

    suspend fun removeProduct(id: String): UiState<Unit> =
        request("Não foi possível remover o produto. Tente novamente.") {
            api.rpc("app_remove_product", buildJsonObject { put("p_id", id) })
            Unit
        }

    suspend fun beginMapSession(sessionId: String): UiState<Unit> =
        mapSession("app_begin_map_session", sessionId)

    suspend fun endMapSession(sessionId: String): UiState<Unit> =
        mapSession("app_end_map_session", sessionId)

    suspend fun publishMapPosition(sessionId: String, location: Location): UiState<Unit> =
        request("Não foi possível enviar sua posição ao mapa.") {
            api.rpc("app_publish_map_position", buildJsonObject {
                put("p_session_id", sessionId)
                put("p_latitude", location.latitude)
                put("p_longitude", location.longitude)
            })
            Unit
        }

    suspend fun mapMarkers(vendorId: String? = null): UiState<List<VendorMapMarker>> =
        request("Não foi possível atualizar o mapa.") {
            api.rpc("app_get_map_markers", buildJsonObject {
                put("p_vendor_id", vendorId?.let(::JsonPrimitive) ?: JsonNull)
            }).decodeList<VendorMapMarker>()
        }

    private suspend fun mapSession(function: String, id: String): UiState<Unit> =
        request("Não foi possível atualizar sua rota no mapa.") {
            api.rpc(function, buildJsonObject { put("p_session_id", id) })
            Unit
        }

    private suspend fun photo(function: String, key: String, id: String): ByteArray? {
        val result = request("Não foi possível carregar a foto.") {
            api.rpc(function, buildJsonObject { put(key, id) }).decodeAs<JsonPrimitive>().contentOrNull?.toPhotoBytes()
        }
        return (result as? UiState.Success)?.data
    }

    private inline fun <T> request(message: String, action: () -> T): UiState<T> = try {
        UiState.Success(action())
    } catch (error: CancellationException) {
        throw error
    } catch (_: Exception) {
        UiState.Error(message)
    }
}

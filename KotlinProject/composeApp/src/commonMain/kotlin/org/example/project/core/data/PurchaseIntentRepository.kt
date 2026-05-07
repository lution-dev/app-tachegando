package org.example.project.core.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.core.utils.Location
import org.example.project.core.utils.UiState

object PurchaseIntentRepository {
    @Serializable
    private data class PurchaseIntentInsert(
        @SerialName("consumer_id") val consumerId: String,
        @SerialName("vendor_id") val vendorId: String,
        val location: String,
        val status: String = "pending"
    )

    suspend fun createArrivalAlert(vendorId: String, location: Location): UiState<Unit> {
        return try {
            val consumerId = supabase.auth.currentUserOrNull()?.id
                ?: return UiState.Error("Entre na sua conta para ativar o aviso.")

            supabase.postgrest["purchase_intents"].insert(
                PurchaseIntentInsert(
                    consumerId = consumerId,
                    vendorId = vendorId,
                    location = "POINT(${location.longitude} ${location.latitude})"
                )
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error("Nao foi possivel ativar o aviso agora.")
        }
    }
}

package lc.fungee.Ingredicheck.foodnotes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Response from GET family/food-notes/all.
 * Matches iOS FoodNotesAllResponse.
 */
@Serializable
data class FoodNotesAllResponse(
    val familyNote: FoodNotesResponse? = null,
    val memberNotes: Map<String, FoodNotesResponse> = emptyMap()
)

/**
 * Single food note (family or member): content + version for optimistic locking.
 * Matches iOS FoodNotesResponse.
 */
@Serializable
data class FoodNotesResponse(
    val content: JsonObject,
    val version: Int,
    @SerialName("updatedAt") val updatedAt: String = ""
)

/**
 * Response from GET family/food-notes/summary.
 * Matches iOS DTO.FoodNotesSummaryResponse.
 */
@Serializable
data class FoodNotesSummaryResponse(
    val summary: String,
    @SerialName("generated_at") val generatedAt: String = "",
    @SerialName("is_cached") val isCached: Boolean = false
)

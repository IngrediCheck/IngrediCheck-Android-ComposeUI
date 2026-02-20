package lc.fungee.Ingredicheck.dietary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One dietary preference item from the backend (same as iOS DTO.DietaryPreference).
 * Used for GET list and for add/edit success response.
 */
@Serializable
data class DietaryPreferenceDto(
    val text: String,
    @SerialName("annotatedText") val annotatedText: String = text,
    val id: Int
)

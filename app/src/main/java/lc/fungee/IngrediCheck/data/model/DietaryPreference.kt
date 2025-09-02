package lc.fungee.IngrediCheck.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DietaryPreference(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("preference_text") val preferenceText: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class NewDietaryPreference(
    @SerialName("user_id") val userId: String,
    @SerialName("preference_text") val preferenceText: String
)
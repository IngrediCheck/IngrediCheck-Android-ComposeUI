package lc.fungee.Ingredicheck.memoji

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemojiRequest(
    val familyType: String,
    val gesture: String,
    val hair: String,
    val skinTone: String,
    val accessories: List<String>,
    val background: String,
    val size: String,
    val model: String,
    val subscriptionTier: String,
    val colorTheme: String? = null,
    val mood: String? = null
)

@Serializable
data class MemojiResponse(
    val success: Boolean,
    val cached: Boolean? = null,
    val imageUrl: String? = null,
    @SerialName("image_url") val imageUrlSnake: String? = null
) {
    val resolvedImageUrl: String?
        get() = imageUrl ?: imageUrlSnake
}

@Serializable
data class MemojiErrorResponse(
    val error: MemojiError
) {
    @Serializable
    data class MemojiError(
        val message: String,
        val details: String? = null
    )
}

package lc.fungee.IngrediCheck.model.model.feedback

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackImageData(
    val imageFileHash: String,
    val imageOCRText: String,
    val barcode: String? = null
)

@Serializable
data class FeedbackData(
    val reasons: List<String>,
    val note: String? = null,
    val images: List<FeedbackImageData>? = null,
    val rating: Int = 0
)

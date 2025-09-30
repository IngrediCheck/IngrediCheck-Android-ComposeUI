package lc.fungee.IngrediCheck.model.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageInfo(
    val imageFileHash: String,
    val imageOCRText: String,
    val barcode: String? = null
)

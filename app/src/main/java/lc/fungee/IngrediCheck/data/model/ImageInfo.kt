package lc.fungee.IngrediCheck.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageInfo(
    val imageFileHash: String,
    val imageOCRText: String,
    val barcode: String? = null
)

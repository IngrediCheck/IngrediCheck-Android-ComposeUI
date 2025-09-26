package lc.fungee.IngrediCheck.ui.screens.check

import lc.fungee.IngrediCheck.data.model.ImageInfo

sealed class CheckSheetState {
    object Scanner : CheckSheetState()
    data class Analysis(
        val barcode: String? = null,
        val images: List<ImageInfo>? = null
    ) : CheckSheetState()
}
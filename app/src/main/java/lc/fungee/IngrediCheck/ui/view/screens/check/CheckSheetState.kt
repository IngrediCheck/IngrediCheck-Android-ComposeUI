package lc.fungee.IngrediCheck.ui.view.screens.check

import lc.fungee.IngrediCheck.model.model.ImageInfo

sealed class CheckSheetState {
    object Scanner : CheckSheetState()
    data class Analysis(
        val barcode: String? = null,
        val images: List<ImageInfo>? = null
    ) : CheckSheetState()
}
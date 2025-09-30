package lc.fungee.IngrediCheck.domain.usecase

import android.content.Context
import java.io.File
import lc.fungee.IngrediCheck.model.source.mlkit.BarcodeScannerService

class DetectBarcodeUseCase(private val service: BarcodeScannerService) {
    suspend operator fun invoke(file: File, context: Context): String? = service.detect(file, context)
}

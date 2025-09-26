package lc.fungee.IngrediCheck.domain.usecase

import android.content.Context
import java.io.File
import lc.fungee.IngrediCheck.data.source.mlkit.TextRecognizerService

class RecognizeTextUseCase(private val service: TextRecognizerService) {
    suspend operator fun invoke(file: File, context: Context): String = service.recognize(file, context)
}

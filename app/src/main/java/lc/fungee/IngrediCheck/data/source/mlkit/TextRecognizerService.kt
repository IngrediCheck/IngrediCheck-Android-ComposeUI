package lc.fungee.IngrediCheck.data.source.mlkit

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.File

interface TextRecognizerService {
    suspend fun recognize(file: File, context: Context): String
}

private fun decodeDownsampled(file: File, maxDim: Int = 2048): android.graphics.Bitmap? {
    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, opts)
    var inSampleSize = 1
    val (w, h) = opts.outWidth to opts.outHeight
    if (w > maxDim || h > maxDim) {
        var halfW = w / 2
        var halfH = h / 2
        while ((halfW / inSampleSize) >= maxDim || (halfH / inSampleSize) >= maxDim) {
            inSampleSize *= 2
        }
    }
    val decodeOpts = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
    return BitmapFactory.decodeFile(file.absolutePath, decodeOpts)
}

class MlKitTextRecognizer : TextRecognizerService {
    override suspend fun recognize(file: File, context: Context): String {
        return try {
            // Downsample to reduce native crashes on some physical devices
            val bmp = decodeDownsampled(file)
            if (bmp == null) {
                Log.e("OCR", "Bitmap decode failed; returning empty text")
                return ""
            }
            val image = InputImage.fromBitmap(bmp, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val result = recognizer.process(image).await()
            result.text
        } catch (e: Exception) {
            Log.e("OCR", "Error during text recognition", e)
            ""
        }
    }
}

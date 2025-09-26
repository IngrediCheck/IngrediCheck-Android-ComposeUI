package lc.fungee.IngrediCheck.data.source.mlkit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.File

interface TextRecognizerService {
    suspend fun recognize(file: File, context: Context): String
}

private fun decodeDownsampled(file: File, maxDim: Int = 2048): Bitmap? {
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

private fun applyExifOrientation(file: File, src: Bitmap): Bitmap {
    val exif = try { ExifInterface(file.absolutePath) } catch (e: Exception) { null }
    val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ?: ExifInterface.ORIENTATION_NORMAL

    val m = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.preScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.preScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> { // flip H + rotate 90
            m.preScale(-1f, 1f)
            m.postRotate(90f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> { // flip H + rotate 270
            m.preScale(-1f, 1f)
            m.postRotate(270f)
        }
        else -> return src
    }

    return try {
        val corrected = Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
        if (corrected != src) src.recycle()
        corrected
    } catch (e: Exception) {
        // Fallback to original if transform fails
        src
    }
}

class MlKitTextRecognizer : TextRecognizerService {
    override suspend fun recognize(file: File, context: Context): String {
        return try {
            // Downsample to reduce native crashes on some physical devices
            val decoded = decodeDownsampled(file)
            if (decoded == null) {
                Log.e("OCR", "Bitmap decode failed; returning empty text")
                return ""
            }
            val bmp = applyExifOrientation(file, decoded)
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

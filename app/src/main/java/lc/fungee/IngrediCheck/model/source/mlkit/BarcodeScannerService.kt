package lc.fungee.IngrediCheck.model.source.mlkit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await
import java.io.File

interface BarcodeScannerService {
    suspend fun detect(file: File, context: Context): String?
}

private fun decodeDownsampled(file: File, maxDim: Int = 1600): Bitmap? {
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
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            m.preScale(-1f, 1f)
            m.postRotate(90f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
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
        src
    }
}

class MlKitBarcodeScanner : BarcodeScannerService {
    override suspend fun detect(file: File, context: Context): String? {
        return try {
            val decoded = decodeDownsampled(file)
            if (decoded == null) {
                Log.e("Barcode", "Bitmap decode failed; returning null")
                return null
            }
            val bmp = applyExifOrientation(file, decoded)
            val image = InputImage.fromBitmap(bmp, 0)
            val scanner = BarcodeScanning.getClient()
            val barcodes = scanner.process(image).await()
            barcodes.firstOrNull { it.rawValue?.isNotBlank() == true &&
                (it.format == Barcode.FORMAT_EAN_8 || it.format == Barcode.FORMAT_EAN_13)
            }?.rawValue
        } catch (t: Throwable) {
            Log.e("Barcode", "Error detecting barcode from image", t)
            null
        }
    }
}

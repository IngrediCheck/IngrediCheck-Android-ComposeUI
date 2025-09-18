package lc.fungee.IngrediCheck.data.source.mlkit

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await
import java.io.File

interface BarcodeScannerService {
    suspend fun detect(file: File, context: Context): String?
}

private fun decodeDownsampled(file: File, maxDim: Int = 1600): android.graphics.Bitmap? {
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

class MlKitBarcodeScanner : BarcodeScannerService {
    override suspend fun detect(file: File, context: Context): String? {
        return try {
            val bmp = decodeDownsampled(file)
            if (bmp == null) {
                Log.e("Barcode", "Bitmap decode failed; returning null")
                return null
            }
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

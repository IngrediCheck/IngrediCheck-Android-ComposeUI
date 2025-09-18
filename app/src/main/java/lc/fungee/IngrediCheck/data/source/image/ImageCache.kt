package lc.fungee.IngrediCheck.data.source.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ImageCache {
    enum class Size(val dir: String, val maxDim: Int) { SMALL("small", 256), MEDIUM("medium", 512), LARGE("large", Int.MAX_VALUE) }

    fun fileFor(context: Context, hash: String, size: Size): File {
        val dir = File(context.cacheDir, "images/${size.dir}")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$hash.jpg")
    }

    suspend fun cacheFromFile(context: Context, hash: String, sourceFile: File): Map<Size, File> = withContext(Dispatchers.IO) {
        val outLarge = fileFor(context, hash, Size.LARGE)
        val outMedium = fileFor(context, hash, Size.MEDIUM)
        val outSmall = fileFor(context, hash, Size.SMALL)
        try {
            // Large = original bytes
            if (!outLarge.exists()) {
                sourceFile.inputStream().use { input ->
                    outLarge.outputStream().use { output -> input.copyTo(output) }
                }
            }

            // Decode once from original for downscales
            val bmp = BitmapFactory.decodeFile(outLarge.absolutePath) ?: return@withContext mapOf(
                Size.LARGE to outLarge
            )

            // Medium
            if (!outMedium.exists()) {
                val m = scaleToMaxDim(bmp, Size.MEDIUM.maxDim)
                outMedium.outputStream().use { os -> m.compress(Bitmap.CompressFormat.JPEG, 85, os) }
            }
            // Small
            if (!outSmall.exists()) {
                val s = scaleToMaxDim(bmp, Size.SMALL.maxDim)
                outSmall.outputStream().use { os -> s.compress(Bitmap.CompressFormat.JPEG, 85, os) }
            }

            mapOf(Size.LARGE to outLarge, Size.MEDIUM to outMedium, Size.SMALL to outSmall)
        } catch (t: Throwable) {
            Log.e("ImageCache", "cacheFromFile failed for $hash", t)
            // Best-effort: return any that exist
            val result = mutableMapOf<Size, File>()
            if (outLarge.exists()) result[Size.LARGE] = outLarge
            if (outMedium.exists()) result[Size.MEDIUM] = outMedium
            if (outSmall.exists()) result[Size.SMALL] = outSmall
            result
        }
    }

    private fun scaleToMaxDim(src: Bitmap, maxDim: Int): Bitmap {
        if (maxDim == Int.MAX_VALUE) return src
        val w = src.width
        val h = src.height
        val maxSide = if (w > h) w else h
        if (maxSide <= maxDim) return src
        val scale = maxSide.toFloat() / maxDim.toFloat()
        val newW = maxOf(1, (w / scale).toInt())
        val newH = maxOf(1, (h / scale).toInt())
        return Bitmap.createScaledBitmap(src, newW, newH, true)
    }
}

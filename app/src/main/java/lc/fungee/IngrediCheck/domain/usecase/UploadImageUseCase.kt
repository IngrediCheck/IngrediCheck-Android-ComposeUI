package lc.fungee.IngrediCheck.domain.usecase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.ByteArrayOutputStream
import lc.fungee.IngrediCheck.data.source.remote.StorageService

class UploadImageUseCase(private val storage: StorageService) {
    suspend operator fun invoke(
        file: File,
        accessToken: String,
        functionsBaseUrl: String,
        anonKey: String
    ): String? {
        // Normalize EXIF orientation so uploaded JPEG pixels are upright everywhere
        val bytes = normalizeJpegBytes(file)
        return storage.uploadJpeg(
            bytes = bytes,
            accessToken = accessToken,
            functionsBaseUrl = functionsBaseUrl,
            anonKey = anonKey
        )
    }
}

private fun normalizeJpegBytes(file: File, quality: Int = 95): ByteArray {
    // Decode full image (you can add downsampling if memory is a concern)
    val src: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
        ?: return file.readBytes()

    val exif = try { ExifInterface(file.absolutePath) } catch (_: Exception) { null }
    val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ?: ExifInterface.ORIENTATION_NORMAL

    val m = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.preScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.preScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> { m.preScale(-1f, 1f); m.postRotate(90f) }
        ExifInterface.ORIENTATION_TRANSVERSE -> { m.preScale(-1f, 1f); m.postRotate(270f) }
        else -> {
            // Already normal; return file bytes directly
            return file.readBytes()
        }
    }

    val corrected = try {
        Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
    } catch (_: Exception) {
        // If transform fails, fall back to original file
        return file.readBytes()
    } finally {
        if (!src.isRecycled) src.recycle()
    }

    return ByteArrayOutputStream().use { out ->
        corrected.compress(Bitmap.CompressFormat.JPEG, quality, out)
        if (!corrected.isRecycled) corrected.recycle()
        out.toByteArray()
    }
}

package lc.fungee.IngrediCheck.model.source.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.exifinterface.media.ExifInterface
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lc.fungee.IngrediCheck.model.entities.ImageLocationInfo
import lc.fungee.IngrediCheck.model.entities.imageFileHash
import java.io.File
import java.net.URL
import kotlin.time.Duration.Companion.seconds

/**
 * Resolve an image into a model suitable for AsyncImage(model = ...): either a String URL or a File.
 *
 * Behavior
 * - If a local cached file exists for the given [image]'s hash and [size], returns that File.
 * - Else, if [image] has a direct URL, returns that URL immediately.
 * - Else, if [image] has an imageFileHash, downloads from Supabase Storage, normalizes EXIF,
 *   caches to images_v2/<size>/<hash>.jpg (with legacy cache fallback), then returns the File.
 * - Returns null if nothing could be resolved.
 */
@Suppress("RedundantNullableReturnType")
suspend fun resolveImage(
    context: Context,
    supabaseClient: SupabaseClient,
    image: ImageLocationInfo,
    size: ImageCache.Size
): Any? = withContext(Dispatchers.IO) {
    val hash = image.imageFileHash
    val directUrl = image.url ?: image.imageUrl

    // 1) Prefer cached file if hash is available
    if (!hash.isNullOrBlank()) {
        cachedFileIfExists(context, hash, size)?.let { return@withContext it }
    }

    // 2) Fall back to direct URL if present (cheap and immediate)
    if (!directUrl.isNullOrBlank()) {
        return@withContext directUrl
    }

    // 3) Download by hash and cache if possible
    if (!hash.isNullOrBlank()) {
        fetchAndCacheByHash(context, supabaseClient, hash, size)?.let { return@withContext it }
    }

    // 4) Nothing resolved
    return@withContext null
}

/** Check both v2 and legacy cache locations */
private fun cachedFileIfExists(context: Context, hash: String, size: ImageCache.Size): File? {
    // New v2 cache (EXIF-corrected)
    val v2 = File(File(context.cacheDir, "images_v2/${size.dir}"), "$hash.jpg")
    if (v2.exists()) return v2

    // Legacy cache (pre-existing in app)
    val legacy = ImageCache.fileFor(context, hash, size)
    return if (legacy.exists()) legacy else null
}

/** Download bytes from Supabase, EXIF-correct + downscale, and cache into v2 path */
private suspend fun fetchAndCacheByHash(
    context: Context,
    supabaseClient: SupabaseClient,
    hash: String,
    size: ImageCache.Size
): File? = withContext(Dispatchers.IO) {
    try {
        val outDir = File(File(context.cacheDir, "images_v2"), size.dir).apply { mkdirs() }
        val outFile = File(outDir, "$hash.jpg")
        if (outFile.exists()) return@withContext outFile

        // If legacy cache exists, return it while keeping v2 generation lazy
        cachedFileIfExists(context, hash, size)?.let { return@withContext it }

        // Signed URL (bucket: productimages)
        val bucket = supabaseClient.storage.from("productimages")
        val signedUrl = runCatching { bucket.createSignedUrl(hash, 3600.seconds) }.getOrNull()
        if (signedUrl.isNullOrBlank()) return@withContext null

        val bytes = URL(signedUrl).openStream().use { it.readBytes() }

        // EXIF orientation
        val orientation = runCatching {
            ExifInterface(bytes.inputStream()).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

        val bmp0: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext null

        // Rotate/flip
        val m = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> { m.preScale(-1f, 1f); m.postRotate(90f) }
            ExifInterface.ORIENTATION_TRANSVERSE -> { m.preScale(-1f, 1f); m.postRotate(270f) }
        }

        val bmpCorrected = runCatching {
            if (m.isIdentity) bmp0 else Bitmap.createBitmap(bmp0, 0, 0, bmp0.width, bmp0.height, m, true)
        }.getOrElse { bmp0 }
        if (bmpCorrected != bmp0 && !bmp0.isRecycled) bmp0.recycle()

        val maxDim = size.maxDim
        val scaled = scaleToMaxDim(bmpCorrected, maxDim)
        outFile.outputStream().use { os ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, os)
        }
        if (scaled != bmpCorrected && !bmpCorrected.isRecycled) bmpCorrected.recycle()
        return@withContext outFile
    } catch (t: Throwable) {
        Log.e("ImageResolver", "fetchAndCacheByHash failed for $hash@${size.dir}", t)
        // Last resort: legacy cache
        return@withContext cachedFileIfExists(context, hash, size)
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

// -------------------- Compose-friendly helper --------------------

@Composable
fun rememberResolvedImageModel(
    image: ImageLocationInfo?,
    supabaseClient: SupabaseClient,
    size: ImageCache.Size
): State<Any?> {
    val context = LocalContext.current
    return produceState<Any?>(initialValue = null, image, supabaseClient, size) {
        value = if (image == null) null else resolveImage(context, supabaseClient, image, size)
    }
}

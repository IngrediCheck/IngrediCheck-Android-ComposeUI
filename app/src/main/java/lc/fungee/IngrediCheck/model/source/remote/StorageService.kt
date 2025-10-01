package lc.fungee.IngrediCheck.model.source.remote

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface StorageService {
    /**
     * Upload JPEG bytes to Supabase storage and return deterministic SHA-256 hash (used as filename).
     * Requires a valid user access token to satisfy RLS.
     */
    suspend fun uploadJpeg(
        bytes: ByteArray,
        accessToken: String,
        functionsBaseUrl: String,
        anonKey: String
    ): String?

    /**
     * Delete a previously uploaded object by its filename (hash) from the 'productimages' bucket.
     */
    suspend fun deleteObject(
        filename: String,
        accessToken: String,
        functionsBaseUrl: String,
        anonKey: String
    ): Boolean
}

class SupabaseStorageService(
    private val client: OkHttpClient
) : StorageService {

    override suspend fun uploadJpeg(
        bytes: ByteArray,
        accessToken: String,
        functionsBaseUrl: String,
        anonKey: String
    ): String? {
        val hash = sha256Hex(bytes)
        val baseUrl = functionsBaseUrl.substringBefore("/functions/")
        val url = "$baseUrl/storage/v1/object/productimages/$hash"
        return withContext(Dispatchers.IO) {
            try {
                val mediaType = "image/jpeg".toMediaTypeOrNull() ?: return@withContext null
                val requestBody = RequestBody.create(mediaType, bytes)
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("apikey", anonKey)
                    .addHeader("x-upsert", "true")
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()
                    if (!response.isSuccessful) {
                        Log.e("StorageService", "Upload failed code=${response.code}, body=${body?.take(200)}")
                        null
                    } else hash
                }
            } catch (e: Exception) {
                Log.e("StorageService", "Exception during upload", e)
                null
            }
        }
    }

    override suspend fun deleteObject(
        filename: String,
        accessToken: String,
        functionsBaseUrl: String,
        anonKey: String
    ): Boolean {
        val baseUrl = functionsBaseUrl.substringBefore("/functions/")
        val url = "$baseUrl/storage/v1/object/productimages/$filename"
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .delete()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("apikey", anonKey)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val body = response.body?.string()
                        Log.e("StorageService", "Delete failed code=${response.code}, body=${body?.take(200)}")
                        false
                    } else true
                }
            } catch (e: Exception) {
                Log.e("StorageService", "Exception during delete", e)
                false
            }
        }
    }

    private fun sha256Hex(data: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(data)
        val sb = StringBuilder()
        for (b in digest) sb.append(String.format("%02x", b))
        return sb.toString()
    }
}

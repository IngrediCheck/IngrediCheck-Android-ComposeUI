package lc.fungee.IngrediCheck.model.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lc.fungee.IngrediCheck.model.entities.SafeEatsEndpoint
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Repository for ping endpoint to measure backend latency
 */
class PingRepository(
    private val functionsBaseUrl: String,
    private val anonKey: String,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .build()
) {
    /**
     * Ping the backend and return latency in milliseconds, or null on failure
     */
    suspend fun ping(token: String): Long? = withContext(Dispatchers.IO) {
        try {
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.PING.format()}"
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()

            val startTime = System.currentTimeMillis()
            client.newCall(request).execute().use { response ->
                val endTime = System.currentTimeMillis()
                val latencyMs = endTime - startTime

                if (response.code == 204) {
                    latencyMs
                } else {
                    Log.w("PingRepository", "Ping failed with status ${response.code}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("PingRepository", "Ping API call failed", e)
            null
        }
    }
}


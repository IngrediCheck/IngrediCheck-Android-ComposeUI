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
            Log.d("PingRepository", "Starting ping to: $url")
            Log.d("PingRepository", "Token length: ${token.length}, Token preview: ${token.take(20)}...")

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()

            val startTime = System.currentTimeMillis()
            Log.d("PingRepository", "Ping request started at: $startTime ms")
            
            client.newCall(request).execute().use { response ->
                val endTime = System.currentTimeMillis()
                val latencyMs = endTime - startTime

                Log.d("PingRepository", "Ping response received at: $endTime ms")
                Log.d("PingRepository", "Ping response code=${response.code}, latency=${latencyMs}ms")
                Log.d("PingRepository", "Response headers: ${response.headers}")

                if (response.code == 204) {
                    Log.i("PingRepository", "✓ Ping successful! Latency: ${latencyMs}ms")
                    latencyMs
                } else {
                    Log.w("PingRepository", "✗ Ping failed with status ${response.code}")
                    val responseBody = response.body?.string()?.take(200)
                    Log.w("PingRepository", "Response body: $responseBody")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("PingRepository", "✗ Ping API call failed with exception", e)
            Log.e("PingRepository", "Exception type: ${e.javaClass.simpleName}, message: ${e.message}")
            null
        }
    }
}


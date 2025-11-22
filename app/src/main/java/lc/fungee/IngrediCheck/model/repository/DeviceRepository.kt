package lc.fungee.IngrediCheck.model.repository

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import lc.fungee.IngrediCheck.model.entities.SafeEatsEndpoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class DeviceRepository(
    private val supabaseClient: SupabaseClient,
    private val functionsBaseUrl: String,
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()
) {

    private val mediaTypeJson = "application/json".toMediaType()

    private fun authToken(): String {
        return supabaseClient.auth.currentSessionOrNull()?.accessToken
            ?: throw IllegalStateException("Not authenticated")
    }

    private fun authRequest(url: String, token: String): Request.Builder {
        return Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
    }

    /**
     * Registers the device and returns the is_internal status from the server response.
     */
    suspend fun registerDevice(deviceId: String, markInternal: Boolean): Boolean = withContext(Dispatchers.IO) {
        val token = authToken()
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.DEVICES_REGISTER.format()}"
        val payload = buildJsonObject {
            put("deviceId", deviceId)
            put("markInternal", markInternal)
        }
        val request = authRequest(url, token)
            .post(payload.toString().toRequestBody(mediaTypeJson))
            .build()

        client.newCall(request).execute().use { resp ->
            val body = resp.body?.string().orEmpty()

            when (resp.code) {
                200 -> {
                    // Parse is_internal from response
                    val element = runCatching { json.parseToJsonElement(body) }.getOrNull()
                    element
                        ?.jsonObject
                        ?.get("is_internal")
                        ?.jsonPrimitive
                        ?.booleanOrNull
                        ?: markInternal // fallback to requested value if parsing fails
                }
                400 -> {
                    Log.e("DeviceRepository", "Invalid device registration request")
                    throw Exception("Invalid device ID or request format")
                }
                401 -> {
                    throw Exception("Authentication required")
                }
                else -> {
                    throw Exception("Failed to register device: ${resp.code}")
                }
            }
        }
    }

    suspend fun markDeviceInternal(deviceId: String): Boolean = withContext(Dispatchers.IO) {
        val token = authToken()
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.DEVICES_MARK_INTERNAL.format()}"
        val payload = buildJsonObject {
            put("deviceId", deviceId)
        }
        val request = authRequest(url, token)
            .post(payload.toString().toRequestBody(mediaTypeJson))
            .build()

        client.newCall(request).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            Log.d("DeviceRepository", "markDeviceInternal code=${resp.code}, body=${body.take(200)}")

            when (resp.code) {
                200 -> {
                    // Success - device marked as internal
                    true
                }
                400 -> {
                    Log.e("DeviceRepository", "Invalid request to mark device internal")
                    throw Exception("Invalid device ID or request format")
                }
                401 -> {
                    throw Exception("Authentication required")
                }
                403 -> {
                    Log.e("DeviceRepository", "Device ownership verification failed")
                    throw Exception("Device does not belong to the authenticated user")
                }
                404 -> {
                    Log.e("DeviceRepository", "Device not registered")
                    throw Exception("Device not found. Please register first.")
                }
                else -> {
                    throw Exception("Failed to mark device internal: ${resp.code}")
                }
            }
        }
    }

    suspend fun isDeviceInternal(deviceId: String): Boolean = withContext(Dispatchers.IO) {
        val token = authToken()
        val path = SafeEatsEndpoint.DEVICES_IS_INTERNAL.format(deviceId)
        val url = "$functionsBaseUrl/$path"
        val request = authRequest(url, token)
            .get()
            .build()

        client.newCall(request).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            Log.d("DeviceRepository", "isDeviceInternal code=${resp.code}, body=${body.take(200)}")

            when (resp.code) {
                200 -> {
                    // Success - parse JSON response
                    val element = runCatching { json.parseToJsonElement(body) }.getOrNull()
                    element
                        ?.jsonObject
                        ?.get("is_internal")
                        ?.jsonPrimitive
                        ?.booleanOrNull
                        ?: false
                }
                404 -> {
                    // Device not registered - treat as not internal
                    Log.d("DeviceRepository", "Device not registered, treating as not internal")
                    false
                }
                403 -> {
                    // Device doesn't belong to user - security issue
                    Log.e("DeviceRepository", "Device ownership verification failed")
                    throw Exception("Device does not belong to the authenticated user")
                }
                else -> {
                    throw Exception("Failed to fetch device status: ${resp.code}")
                }
            }
        }
    }
}



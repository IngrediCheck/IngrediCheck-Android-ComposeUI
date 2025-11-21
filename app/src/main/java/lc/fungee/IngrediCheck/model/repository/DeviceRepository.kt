package lc.fungee.IngrediCheck.model.repository

import android.util.Log
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
    private val preferenceRepository: PreferenceRepository,
    private val functionsBaseUrl: String,
    private val anonKey: String,
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()
) {

    private val mediaTypeJson = "application/json".toMediaType()

    private suspend fun authToken(): String {
        return preferenceRepository.currentToken()
            ?: throw IllegalStateException("Not authenticated")
    }

    private fun authRequest(url: String, token: String): Request.Builder {
        return Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", anonKey)
    }

    suspend fun registerDevice(deviceId: String, isInternal: Boolean): Boolean = withContext(Dispatchers.IO) {
        val token = authToken()
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.DEVICES_REGISTER.format()}"
        val payload = buildJsonObject {
            put("deviceId", deviceId)
            put("isInternal", isInternal)
        }
        val request = authRequest(url, token)
            .post(payload.toString().toRequestBody(mediaTypeJson))
            .build()

        client.newCall(request).execute().use { resp ->
            val bodyPreview = resp.body?.string().orEmpty()
            Log.d("DeviceRepository", "registerDevice code=${resp.code}, body=${bodyPreview.take(200)}")
            if (resp.code !in listOf(200, 201, 204)) {
                throw Exception("Failed to register device: ${resp.code}")
            }
            true
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
            val bodyPreview = resp.body?.string().orEmpty()
            Log.d("DeviceRepository", "markDeviceInternal code=${resp.code}, body=${bodyPreview.take(200)}")
            if (resp.code !in listOf(200, 201, 204)) {
                throw Exception("Failed to mark device internal: ${resp.code}")
            }
            true
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
            if (resp.code !in listOf(200, 201, 204)) {
                throw Exception("Failed to fetch device status: ${resp.code}")
            }
            if (body.isBlank()) {
                false
            } else {
                val element = runCatching { json.parseToJsonElement(body) }.getOrNull()
                element
                    ?.jsonObject
                    ?.get("isInternal")
                    ?.jsonPrimitive
                    ?.booleanOrNull
                    ?: element
                        ?.jsonObject
                        ?.get("is_internal")
                        ?.jsonPrimitive
                        ?.booleanOrNull
                    ?: false
            }
        }
    }
}



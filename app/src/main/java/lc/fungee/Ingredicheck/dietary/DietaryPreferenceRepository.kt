package lc.fungee.Ingredicheck.dietary

import android.util.Log
import lc.fungee.Ingredicheck.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.isSuccess
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import lc.fungee.Ingredicheck.AppConfig


private const val TAG = "DietaryPreference"

/**
 * Dietary Preference = the user's food preferences (allergies, intolerances, etc.) stored on the backend.
 *
 * Same as iOS: one list per user (add-family and just-me both use the same API).
 * - When the user finishes the fine-tune flow ("All Set!" or last step), we build one text from
 *   all selected chips (e.g. "Peanuts, Tree nuts, Lactose") and POST it as one new preference.
 * - Backend: GET/POST preferencelists/default, PUT/DELETE preferencelists/default/{id}.
 */
class DietaryPreferenceRepository {

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
        engine {
            config {
                connectTimeout(15, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
            }
        }
    }

    private fun baseUrl(path: String): String {
        val base = AppConfig.supabaseFunctionsURLBase
        return if (base.endsWith("/")) base + path else "$base$path"
    }

    private fun authHeaders(accessToken: String): Map<String, String> = mapOf(
        "apikey" to AppConfig.supabaseKey,
        "Authorization" to "Bearer $accessToken"
    )

    /** GET list of dietary preferences. */
    suspend fun getDietaryPreferences(accessToken: String): Result<List<DietaryPreferenceDto>> {
        val url = baseUrl("preferencelists/default")
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getDietaryPreferences: GET $url")
        }
        return runCatching {
            val response: HttpResponse = client.get(url) {
                authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
            }
            val body = response.bodyAsText()
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "getDietaryPreferences: status=${response.status.value} bodyLength=${body.length}")
            }
            if (!response.status.isSuccess()) {
                Log.e(TAG, "getDietaryPreferences: failed ${response.status.value} $body")
                throw IllegalStateException("GET failed: ${response.status.value}")
            }
            json.decodeFromString<List<DietaryPreferenceDto>>(body)
        }.onSuccess { list ->
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "getDietaryPreferences: success count=${list.size}")
            }
        }.onFailure { e ->
            Log.e(TAG, "getDietaryPreferences: error", e)
        }
    }

    /**
     * Add (id=null) or edit (id!=null) one dietary preference.
     * Form body: clientActivityId, preference; same as iOS.
     */
    suspend fun addOrEditDietaryPreference(
        accessToken: String,
        clientActivityId: String,
        preferenceText: String,
        id: Int?
    ): Result<PreferenceValidationResult> {
        val path = if (id != null) "preferencelists/default/$id" else "preferencelists/default"
        val method = if (id != null) "PUT" else "POST"
        val url = baseUrl(path)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "addOrEditDietaryPreference: $method $url clientActivityId=$clientActivityId preferenceLength=${preferenceText.length} id=$id")
        }
        return runCatching {
            val formBody = MultiPartFormDataContent(formData {
                append("clientActivityId", clientActivityId)
                append("preference", preferenceText)
            })
            val response = if (id != null) {
                client.put(url) {
                    authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                    accept(ContentType.Application.Json)
                    setBody(formBody)
                }
            } else {
                client.post(url) {
                    authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                    accept(ContentType.Application.Json)
                    setBody(formBody)
                }
            }
            val res = response as HttpResponse
            val body = res.bodyAsText()
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "addOrEditDietaryPreference: status=${res.status.value} bodyLength=${body.length}")
            }
            if (!res.status.isSuccess() && res.status.value !in 200..299 && res.status.value != 422) {
                Log.e(TAG, "addOrEditDietaryPreference: bad status ${res.status.value} $body")
                throw IllegalStateException("Request failed: ${res.status.value}")
            }
            parseValidationResult(body)
        }.onSuccess { result ->
            when (result) {
                is PreferenceValidationResult.Success ->
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "addOrEditDietaryPreference: success id=${result.preference.id}")
                    }
                is PreferenceValidationResult.Failure ->
                    Log.w(TAG, "addOrEditDietaryPreference: failure explanation=${result.explanation}")
            }
        }.onFailure { e ->
            Log.e(TAG, "addOrEditDietaryPreference: error", e)
        }
    }

    /** DELETE one dietary preference. */
    suspend fun deleteDietaryPreference(
        accessToken: String,
        clientActivityId: String,
        id: Int
    ): Result<Unit> {
        val url = baseUrl("preferencelists/default/$id")
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "deleteDietaryPreference: DELETE $url id=$id")
        }
        return runCatching {
            val formBody = MultiPartFormDataContent(formData {
                append("clientActivityId", clientActivityId)
            })
            val response: HttpResponse = client.delete(url) {
                authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                setBody(formBody)
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "deleteDietaryPreference: status=${response.status.value}")
            }
            if (response.status.value != 204 && !response.status.isSuccess()) {
                throw IllegalStateException("DELETE failed: ${response.status.value}")
            }
            Unit
        }.onFailure { e ->
            Log.e(TAG, "deleteDietaryPreference: error", e)
        }
    }

    private fun parseValidationResult(body: String): PreferenceValidationResult {
        val obj = json.parseToJsonElement(body).jsonObject
        val result = obj["result"]?.jsonPrimitive?.content
        return when (result) {
            "success" -> {
                val text = obj["text"]?.jsonPrimitive?.content ?: ""
                val annotatedText = obj["annotatedText"]?.jsonPrimitive?.content ?: text
                val id = obj["id"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                PreferenceValidationResult.Success(DietaryPreferenceDto(text, annotatedText, id))
            }
            "failure" -> {
                val explanation = obj["explanation"]?.jsonPrimitive?.content ?: "Unknown error"
                PreferenceValidationResult.Failure(explanation)
            }
            else -> PreferenceValidationResult.Failure("Unexpected result: $result")
        }
    }
}

/** Result of add/edit: either success with the saved item or failure with message. */
sealed class PreferenceValidationResult {
    data class Success(val preference: DietaryPreferenceDto) : PreferenceValidationResult()
    data class Failure(val explanation: String) : PreferenceValidationResult()
}

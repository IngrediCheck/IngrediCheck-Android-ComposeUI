package lc.fungee.Ingredicheck.foodnotes

import android.util.Log
import com.russhwolf.settings.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import java.util.concurrent.TimeUnit
import kotlinx.serialization.Serializable

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import lc.fungee.Ingredicheck.AppConfig
import lc.fungee.Ingredicheck.foodnotes.FoodNotesSummaryResponse


private const val TAG = "FoodNotes"
private const val MAX_VERSION_RETRY = 2

/**
 * Food-notes API: per-member and "Everyone" chip selections (allergies, intolerances, etc.).
 * Matches iOS: PUT family/food-notes for Everyone, PUT family/members/{id}/food-notes per member.
 */
class FoodNotesRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

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

    // iOS calls Food Notes summary on Fly.io (Config.flyIOBaseURL + "/family/food-notes/summary").
    // Mirror that here instead of using Supabase functions base.
    private fun summaryUrl(): String {
        val base = AppConfig.flyIOBaseURL
        return if (base.endsWith("/")) base + "family/food-notes/summary"
        else "$base/family/food-notes/summary"
    }

    private fun authHeaders(accessToken: String): Map<String, String> = mapOf(
        "apikey" to AppConfig.supabaseKey,
        "Authorization" to "Bearer $accessToken",
        "Content-Type" to "application/json"
    )

    @Serializable
    data class FoodNotesItem(val name: String, val iconName: String = "")

    @Serializable
    data class FoodNotesUpdateRequest(
        val content: Map<String, List<FoodNotesItem>>,
        val version: Int
    )

    /**
     * Convert onboarding content (stepId -> list of { name, iconName }) to API request format.
     */
    private fun toRequestContent(content: Map<String, List<Map<String, String>>>): Map<String, List<FoodNotesItem>> =
        content.mapValues { (_, list) ->
            list.map { m -> FoodNotesItem(name = m["name"] ?: "", iconName = m["iconName"] ?: "") }
        }

    /**
     * GET family/food-notes/all – load family note + all member notes (for versions/cache).
     */
    suspend fun fetchFoodNotesAll(accessToken: String): Result<FoodNotesAllResponse?> {
        val url = baseUrl("family/food-notes/all")
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "fetchFoodNotesAll: GET $url")
        }
        return runCatching {
            val response: HttpResponse = client.get(url) {
                authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
            }
            val body = response.bodyAsText()
            when {
                response.status.value == 404 -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "FoodNotes API: fetchFoodNotesAll 404, no notes yet")
                    }
                    return@runCatching null
                }
                body.isBlank() || body.trim() == "null" -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "FoodNotes API: fetchFoodNotesAll null/empty body")
                    }
                    return@runCatching null
                }
                !response.status.isSuccess() -> {
                    Log.e(TAG, "FoodNotes API: fetchFoodNotesAll failed ${response.status.value} $body")
                    throw IllegalStateException("GET failed: ${response.status.value}")
                }
                else -> {
                    val parsed = json.decodeFromString<FoodNotesAllResponse>(body)
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "FoodNotes API implementation: fetchFoodNotesAll success — data loaded")
                    }
                    parsed
                }
            }
        }.onFailure { e -> Log.e(TAG, "FoodNotes API: fetchFoodNotesAll error", e) }
    }

    /**
     * GET family/food-notes/summary – AI-generated text summary of food notes.
     * Returns null on 404 or empty/null body, matching iOS behavior.
     */
    suspend fun fetchFoodNotesSummary(accessToken: String): Result<FoodNotesSummaryResponse?> {
        val url = summaryUrl()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "fetchFoodNotesSummary: GET $url")
        }
        return runCatching {
            val response: HttpResponse = client.get(url) {
                authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
            }
            val body = response.bodyAsText()
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "fetchFoodNotesSummary: status=${response.status.value} rawBody=${body.take(200)}")
            }
            when {
                response.status.value == 404 -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "FoodNotes API: fetchFoodNotesSummary 404, no notes yet")
                    }
                    return@runCatching null
                }
                body.isBlank() || body.trim() == "null" -> {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "FoodNotes API: fetchFoodNotesSummary empty/null body")
                    }
                    return@runCatching null
                }
                !response.status.isSuccess() -> {
                    Log.e(TAG, "FoodNotes API: fetchFoodNotesSummary failed ${response.status.value} $body")
                    throw IllegalStateException("GET summary failed: ${response.status.value}")
                }
                else -> {
                    val parsed = json.decodeFromString<FoodNotesSummaryResponse>(body)
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "FoodNotes API implementation: fetchFoodNotesSummary success — summary=${parsed.summary.take(80)}")
                    }
                    parsed
                }
            }
        }.onFailure { e -> Log.e(TAG, "FoodNotes API: fetchFoodNotesSummary error", e) }
    }

    /**
     * PUT family/food-notes – update "Everyone" / family note.
     * On 409 (version_mismatch), retries once with currentNote.version if present, else version=0.
     * @param content Map from step id to list of { "name", "iconName" } (e.g. from OnboardingChipData.buildFoodNotesContentFromChipIds).
     */
    suspend fun updateFamilyFoodNotes(
        accessToken: String,
        content: Map<String, List<Map<String, String>>>,
        version: Int,
        retryCount: Int = 0
    ): Result<FoodNotesResponse> {
        val requestContent = toRequestContent(content)
        val url = baseUrl("family/food-notes")
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "FoodNotes API: updateFamilyFoodNotes PUT $url version=$version")
        }
        return runCatching {
            val body = json.encodeToString(FoodNotesUpdateRequest(requestContent, version))
            val response: HttpResponse = client.put(url) {
                authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
                setBody(body)
            }
            val responseBody = response.bodyAsText()
            if (response.status.isSuccess()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "FoodNotes API implementation: updateFamilyFoodNotes success (Everyone) — working")
                }
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "FoodNotes API: updateFamilyFoodNotes status=${response.status.value}")
            }
            when {
                response.status.isSuccess() -> parseFoodNotesResponse(responseBody)
                response.status.value == 409 -> {
                    if (retryCount >= MAX_VERSION_RETRY) {
                        Log.w(TAG, "updateFamilyFoodNotes: 409 conflict after $MAX_VERSION_RETRY retries, giving up")
                        throw IllegalStateException("version conflict (family) after $MAX_VERSION_RETRY retries")
                    }
                    val currentVersion = parseVersionFrom409Response(responseBody)
                    val nextVersion = currentVersion ?: 0
                    if (BuildConfig.DEBUG) {
                        Log.d(
                            TAG,
                            "updateFamilyFoodNotes: 409 retry=${retryCount + 1} with version=$nextVersion (currentVersion=$currentVersion)"
                        )
                    }
                    updateFamilyFoodNotes(
                        accessToken = accessToken,
                        content = content,
                        version = nextVersion,
                        retryCount = retryCount + 1
                    ).getOrThrow()
                }
                else -> throw IllegalStateException("PUT failed: ${response.status.value} $responseBody")
            }
        }.onFailure { e -> Log.e(TAG, "updateFamilyFoodNotes: error", e) }
    }

    /**
     * PUT family/members/{memberId}/food-notes – update one member's note.
     * @param content Map from step id to list of { "name", "iconName" } (e.g. from OnboardingChipData.buildFoodNotesContentFromChipIds).
     */
    suspend fun updateMemberFoodNotes(
        accessToken: String,
        memberId: String,
        content: Map<String, List<Map<String, String>>>,
        version: Int,
        retryCount: Int = 0
    ): Result<FoodNotesResponse> {
        val requestContent = toRequestContent(content)
        val url = baseUrl("family/members/$memberId/food-notes")
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "FoodNotes API: updateMemberFoodNotes PUT $url version=$version")
        }
        return runCatching {
            val body = json.encodeToString(FoodNotesUpdateRequest(requestContent, version))
            val response: HttpResponse = client.put(url) {
                authHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
                setBody(body)
            }
            val responseBody = response.bodyAsText()
            if (response.status.isSuccess()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "FoodNotes API implementation: updateMemberFoodNotes success (memberId=$memberId) — working")
                }
            } else if (BuildConfig.DEBUG) {
                Log.w(
                    TAG,
                    "FoodNotes API: updateMemberFoodNotes failure " +
                            "status=${response.status.value} memberId=$memberId body=$responseBody"
                )
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "FoodNotes API: updateMemberFoodNotes status=${response.status.value}")
            }
            when {
                response.status.isSuccess() -> parseFoodNotesResponse(responseBody)
                response.status.value == 409 -> {
                    if (retryCount >= MAX_VERSION_RETRY) {
                        Log.w(TAG, "updateMemberFoodNotes: 409 conflict after $MAX_VERSION_RETRY retries (memberId=$memberId), giving up")
                        throw IllegalStateException("version conflict (memberId=$memberId) after $MAX_VERSION_RETRY retries")
                    }
                    val currentVersion = parseVersionFrom409Response(responseBody)
                    val nextVersion = currentVersion ?: 0
                    if (BuildConfig.DEBUG) {
                        Log.d(
                            TAG,
                            "updateMemberFoodNotes: 409 retry=${retryCount + 1} with version=$nextVersion (currentVersion=$currentVersion)"
                        )
                    }
                    updateMemberFoodNotes(
                        accessToken = accessToken,
                        memberId = memberId,
                        content = content,
                        version = nextVersion,
                        retryCount = retryCount + 1
                    ).getOrThrow()
                }
                else -> throw IllegalStateException("PUT failed: ${response.status.value} $responseBody")
            }
        }.onFailure { e -> Log.e(TAG, "updateMemberFoodNotes: error", e) }
    }

    private fun parseFoodNotesResponse(body: String): FoodNotesResponse {
        val obj = json.parseToJsonElement(body).jsonObject
        val version = obj["version"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        val updatedAt = obj["updatedAt"]?.jsonPrimitive?.content ?: ""
        val content = obj["content"]?.jsonObject ?: buildJsonObject { }
        return FoodNotesResponse(content = content, version = version, updatedAt = updatedAt)
    }

    private fun parseVersionFrom409Response(body: String): Int? {
        return try {
            val obj = json.parseToJsonElement(body).jsonObject
            val currentNote = obj["currentNote"]?.jsonObject ?: return null
            currentNote["version"]?.jsonPrimitive?.content?.toIntOrNull()
        } catch (_: Exception) {
            null
        }
    }
}

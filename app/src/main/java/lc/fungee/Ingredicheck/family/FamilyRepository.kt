package lc.fungee.Ingredicheck.family

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import lc.fungee.Ingredicheck.AppConfig

class FamilyRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val client: HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }

        engine {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
            }
        }
    }

    private fun url(path: String): String {
        val base = AppConfig.supabaseFunctionsURLBase
        return if (base.endsWith("/")) base + path else base + path
    }

    private fun baseRequestHeaders(accessToken: String): Map<String, String> {
        return mapOf(
            "apikey" to AppConfig.supabaseKey,
            "Authorization" to "Bearer $accessToken",
            "Content-Type" to ContentType.Application.Json.toString()
        )
    }

    private fun decodeFamilyDtoOrNull(responseText: String): FamilyDto? {
        if (responseText.isBlank()) return null

        return try {
            // Most common: response is directly FamilyDto JSON.
            json.decodeFromString<FamilyDto>(responseText)
        } catch (_: SerializationException) {
            // Common alternative: backend wraps payload.
            // Examples: {"family": {...}} or {"data": {...}}
            val root: JsonElement = try {
                json.parseToJsonElement(responseText)
            } catch (_: SerializationException) {
                return null
            }

            val obj: JsonObject = (root as? JsonObject) ?: return null
            val familyEl = obj["family"] ?: obj["data"]
            if (familyEl != null) {
                return try {
                    json.decodeFromJsonElement<FamilyDto>(familyEl)
                } catch (_: SerializationException) {
                    null
                }
            }

            null
        }
    }

    suspend fun createFamily(accessToken: String, request: CreateFamilyRequest): Result<FamilyDto> {
        val body = json.encodeToString(request)
        return runCatching {
            val response: HttpResponse = client.post(url("family")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
                setBody(body)
            }
            val responseText = response.bodyAsText()

            if (!response.status.isSuccess()) {
                val apiError = runCatching { json.decodeFromString<ApiErrorResponse>(responseText) }.getOrNull()
                val msg = apiError?.message ?: apiError?.error ?: "HTTP ${response.status.value}"

                val raw = (apiError?.error ?: "") + " " + (apiError?.message ?: "") + " " + responseText
                // If the backend says:
                // - member PK already exists, OR
                // - user is already part of a family,
                // it usually means the family already exists. Treat this as recoverable
                // and fall back to GET /family, just like iOS does.
                if (raw.contains("members_pkey", ignoreCase = true) ||
                    raw.contains("duplicate key value", ignoreCase = true) ||
                    raw.contains("already part of a family", ignoreCase = true)
                ) {
                    val familyResult = getFamily(accessToken)
                    familyResult.getOrNull()?.let { return@runCatching it }
                }

                throw IllegalStateException("createFamily failed: $msg | body=$responseText")
            }

            val decoded = decodeFamilyDtoOrNull(responseText)
            if (decoded != null) return@runCatching decoded

            // If the body doesn't look like FamilyDto (e.g. empty, wrapper mismatch),
            // fallback to fetching the current family via GET /family.
            val familyResult = getFamily(accessToken)
            familyResult.getOrNull()
                ?: throw MissingFieldException(
                    missingFields = listOf("name", "selfMember"),
                    serialName = "FamilyDto"
                )
        }
    }

    suspend fun getFamily(accessToken: String): Result<FamilyDto> {
        return runCatching {
            val response: HttpResponse = client.get(url("family")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
            }

            val responseText = response.bodyAsText()

            if (!response.status.isSuccess()) {
                val apiError = runCatching { json.decodeFromString<ApiErrorResponse>(responseText) }.getOrNull()
                val msg = apiError?.message ?: apiError?.error ?: "HTTP ${response.status.value}"
                throw IllegalStateException("getFamily failed: $msg | body=$responseText")
            }

            decodeFamilyDtoOrNull(responseText)
                ?: throw IllegalStateException("getFamily returned unexpected JSON: $responseText")
        }
    }

    suspend fun joinFamily(accessToken: String, inviteCode: String): Result<FamilyDto> {
        val body = json.encodeToString(JoinFamilyRequest(inviteCode = inviteCode))
        return runCatching {
            val responseText = client.post(url("family/join")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
                setBody(body)
            }.bodyAsText()

            json.decodeFromString<FamilyDto>(responseText)
        }
    }

    suspend fun leaveFamily(accessToken: String): Result<Unit> {
        return runCatching {
            client.post(url("family/leave")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
            }
            Unit
        }
    }

    suspend fun addMember(accessToken: String, member: FamilyMemberDto): Result<FamilyDto> {
        val body = json.encodeToString(member)
        return runCatching {
            val responseText = client.post(url("family/members")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
                setBody(body)
            }.bodyAsText()

            json.decodeFromString<FamilyDto>(responseText)
        }
    }

    suspend fun invite(accessToken: String, memberId: String): Result<String> {
        val body = json.encodeToString(InviteRequest(memberId = memberId))

        suspend fun requestOnce(): String? {
            val response: HttpResponse = client.post(url("family/invite")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
                setBody(body)
            }
            val responseText = response.bodyAsText()
            Log.d(
                "FamilyRepository",
                "invite response status=${response.status.value}, body=$responseText"
            )
            if (responseText.isBlank()) {
                return null
            }
            return try {
                json.decodeFromString<InviteResponse>(responseText).inviteCode
            } catch (_: MissingFieldException) {
                null
            }
        }

        return runCatching {
            // First attempt
            requestOnce() ?: run {
                // Mirror iOS behavior: retry a couple of times if body is empty
                var code: String? = null
                repeat(2) { attempt ->
                    // 1s, then 2s
                    delay(1_000L * (attempt + 1))
                    code = requestOnce()
                    if (code != null) return@run code!!
                }
                // If still no code, throw a meaningful error (cannot construct MissingFieldException directly)
                throw IllegalStateException("Invite response did not contain inviteCode after retries")
            }
        }
    }

    suspend fun editMember(accessToken: String, memberId: String, member: FamilyMemberDto): Result<FamilyDto> {
        val body = json.encodeToString(member)
        return runCatching {
            val responseText = client.patch(url("family/members/$memberId")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
                setBody(body)
            }.bodyAsText()

            json.decodeFromString<FamilyDto>(responseText)
        }
    }

    suspend fun deleteMember(accessToken: String, memberId: String): Result<FamilyDto> {
        return runCatching {
            val responseText = client.delete(url("family/members/$memberId")) {
                baseRequestHeaders(accessToken).forEach { (k, v) -> header(k, v) }
                accept(ContentType.Application.Json)
            }.bodyAsText()

            json.decodeFromString<FamilyDto>(responseText)
        }
    }
}

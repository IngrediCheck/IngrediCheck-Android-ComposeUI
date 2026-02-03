package lc.fungee.Ingredicheck.memoji

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import lc.fungee.Ingredicheck.AppConfig

class MemojiRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val client: HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            // Edge functions can be slow (image generation / cold start). Give it enough time.
            requestTimeoutMillis = 180_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 180_000
        }

        engine {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(180, TimeUnit.SECONDS)
                writeTimeout(180, TimeUnit.SECONDS)
            }
        }
    }

    suspend fun generateMemoji(
        accessToken: String,
        request: MemojiRequest
    ): Result<MemojiResponse> {
        val url = AppConfig.supabaseFunctionsURLBase + "memoji"
        val payload = json.encodeToString(request)

        return runCatching {
            var lastTimeout: SocketTimeoutException? = null

            repeat(2) { attempt ->
                try {
                    val responseText = client.post(url) {
                        header("apikey", AppConfig.supabaseKey)
                        header("Authorization", "Bearer $accessToken")
                        accept(ContentType.Application.Json)
                        header("Content-Type", ContentType.Application.Json.toString())
                        setBody(payload)
                    }.bodyAsText()

                    val parsed = json.decodeFromString<MemojiResponse>(responseText)

                    if (!parsed.success) {
                        val errorResponse = runCatching {
                            json.decodeFromString<MemojiErrorResponse>(responseText)
                        }.getOrNull()

                        val message = errorResponse?.error?.message ?: "Memoji generation failed"
                        throw IllegalStateException(message)
                    }

                    return@runCatching parsed
                } catch (e: SocketTimeoutException) {
                    lastTimeout = e
                    if (attempt == 0) return@repeat
                    throw e
                }
            }

            throw lastTimeout ?: IllegalStateException("Socket timeout")
        }
    }
}

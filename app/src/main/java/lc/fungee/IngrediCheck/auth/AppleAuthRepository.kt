
package lc.fungee.IngrediCheck.auth
import android.app.Activity
import android.content.Context
import android.content.Intent
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import android.net.Uri
import com.google.gson.Gson

class AppleAuthRepository(
    private val context: Context,
    private val supabaseUrl: String,
    private val supabaseAnonKey: String
) {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    fun launchAppleLoginWebView(activity: Activity) {
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("wqidjkpfdrvomfkmefqc.supabase.co")
            .appendPath("auth")
            .appendPath("v1")
            .appendPath("authorize")
            .appendQueryParameter("provider", "apple")
            .appendQueryParameter("redirect_to", "https://wqidjkpfdrvomfkmefqc.supabase.co/auth/v1/callback")
            .build()
            .toString()

        val intent = Intent(activity, AppleLoginWebViewActivity::class.java).apply {
            putExtra("auth_url", authUrl)
            putExtra("redirect_uri", "https://wqidjkpfdrvomfkmefqc.supabase.co/auth/v1/callback")
        }

        activity.startActivityForResult(intent, 1002)
    }

    suspend fun exchangeAppleCodeWithSupabase(code: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=authorization_code") {
                headers.append("apikey", supabaseAnonKey)
                headers.append("Content-Type", "application/json")
                setBody(
                    mapOf(
                        "grant_type" to "authorization_code",
                        "code" to code,
                        "redirect_uri" to "https://wqidjkpfdrvomfkmefqc.supabase.co/auth/v1/callback"
                    )
                )
            }
            if (response.status.value in 200..299) {
                val responseBody = response.bodyAsText()
                println("Supabase response: $responseBody")
                val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
                Result.success(session)
            } else {
                Result.failure(Exception("Supabase error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exchangeAppleIdTokenWithSupabase(idToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=id_token") {
                headers.append("apikey", supabaseAnonKey)
                headers.append("Content-Type", "application/json")
                setBody(
                    mapOf(
                        "provider" to "apple",
                        "id_token" to idToken
                    )
                )
            }
            if (response.status.value in 200..299) {
                val responseBody = response.bodyAsText()
                println("Supabase response: $responseBody")
                val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
                Result.success(session)
            } else {
                Result.failure(Exception("Supabase error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exchangeGoogleIdTokenWithSupabase(idToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=id_token") {
                headers.append("apikey", supabaseAnonKey)
                headers.append("Content-Type", "application/json")
                setBody(
                    mapOf(
                        "provider" to "google",
                        "id_token" to idToken
                    )
                )
            }
            if (response.status.value in 200..299) {
                val responseBody = response.bodyAsText()
                println("Supabase response: $responseBody")
                val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
                Result.success(session)
            } else {
                Result.failure(Exception("Supabase error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
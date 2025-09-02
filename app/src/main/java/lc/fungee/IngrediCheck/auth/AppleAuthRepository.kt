
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
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.base.Functions
import com.google.gson.Gson
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import lc.fungee.IngrediCheck.data.model.SupabaseSession
// Add OkHttp imports for anonymous sign-in
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull


class AppleAuthRepository(
    private val context: Context,
    private val supabaseUrl: String,
    private val supabaseAnonKey: String
) {
    val supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseAnonKey
    ) {
        install(Auth)
        {
            sessionManager = SharedPreferencesSessionManager(context)
        }// Enables authentication


        install(Postgrest) // Enables PostgREST database calls
    }
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    
    // Add separate OkHttp client for anonymous sign-in
    private val okHttpClient = OkHttpClient()

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
    suspend fun exchangeAppleRefreshTokenWithSupabase(refreshToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=refresh_token") {
                headers.append("apikey", supabaseAnonKey)
                headers.append("Content-Type", "application/json")
                setBody(
                    mapOf(
                        "refresh_token" to refreshToken
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


    suspend fun exchangeAppleAccessTokenWithSupabase(accessToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AppleAuthRepository", "Making request to: $supabaseUrl/auth/v1/user")
            val response: HttpResponse = client.get("$supabaseUrl/auth/v1/user") {
                headers.append("apikey", supabaseAnonKey)
                headers.append("Authorization", "Bearer $accessToken")
            }
            android.util.Log.d("AppleAuthRepository", "Response status: ${response.status}")
            
            if (response.status.value in 200..299) {
                val responseBody = response.bodyAsText()
                android.util.Log.d("AppleAuthRepository", "Supabase response: $responseBody")
                try {
                    val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
                    Result.success(session)
                } catch (e: Exception) {
                    android.util.Log.e("AppleAuthRepository", "JSON parsing error", e)
                    Result.failure(Exception("Invalid response format: ${e.message}"))
                }
            } else {
                val errorBody = response.bodyAsText()
                android.util.Log.e("AppleAuthRepository", "Supabase error response: $errorBody")
                Result.failure(Exception("Supabase error ${response.status}: $errorBody"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AppleAuthRepository", "Network error", e)
            Result.failure(e)
        }
    }
    // ✅ Anonymous Sign-In Method - Correct Supabase Implementation
    suspend fun signInAnonymously(): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AppleAuthRepository", "Starting anonymous sign-in")
            
            // ✅ CORRECT: Empty body or minimal data for anonymous user
            // No email/password - this creates a true anonymous user
            val jsonBody = """
            {
                "data": {
                    "is_anonymous": true
                }
            }
            """.trimIndent()
            
            android.util.Log.d("AppleAuthRepository", "JSON body: $jsonBody")
            
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                jsonBody
            )
            
            val request = Request.Builder()
                .url("$supabaseUrl/auth/v1/signup")
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                android.util.Log.d("AppleAuthRepository", "Anonymous sign-in response: $responseBody")
                try {
                    val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
                    android.util.Log.d("AppleAuthRepository", "Anonymous sign-in successful")
                    Result.success(session)
                } catch (e: Exception) {
                    android.util.Log.e("AppleAuthRepository", "Error parsing anonymous session", e)
                    Result.failure(e)
                }
            } else {
                val errorBody = response.body?.string() ?: ""
                android.util.Log.e("AppleAuthRepository", "Anonymous sign-in failed: ${response.code}, error: $errorBody")
                Result.failure(Exception("Anonymous sign-in failed: ${response.code}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AppleAuthRepository", "Network error during anonymous sign-in", e)
            Result.failure(e)
        }
    }
}
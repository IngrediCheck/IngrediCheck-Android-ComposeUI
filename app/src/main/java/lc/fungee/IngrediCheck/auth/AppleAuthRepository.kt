
package lc.fungee.IngrediCheck.auth
import android.app.Activity
import android.content.Context
import android.content.Intent
import net.openid.appauth.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import android.app.Dialog
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.Uri
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
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

    private val authService by lazy { AuthorizationService(context) }

    fun getAppleAuthRequest(): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            android.net.Uri.parse("https://appleid.apple.com/auth/authorize"),
            android.net.Uri.parse("https://appleid.apple.com/auth/token")
        )
        val redirectUri = android.net.Uri.parse("https://wqidjkpfdrvomfkmefqc.supabase.co/auth/v1/callback")
        return AuthorizationRequest.Builder(
            serviceConfig,
            "llc.fungee.ingredicheck.web", // Service ID from Apple
            ResponseTypeValues.CODE,
            redirectUri
        )
            .setScopes("name", "email")
            .setResponseMode("form_post")
            .build()
    }

    fun performAuthRequest(activity: Activity, request: AuthorizationRequest) {
        val intent = authService.getAuthorizationRequestIntent(request)
        activity.startActivityForResult(intent, 1001)
        // Handle the result in your Activity's onActivityResult or Activity Result API
    }

    suspend fun exchangeCodeWithSupabase(code: String, redirectUri: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=pkce") {
                headers.append("apikey", supabaseAnonKey)
                headers.append("Content-Type", "application/json")
                setBody(
                    mapOf(
                        "grant_type" to "authorization_code",
                        "code" to code,
                        "redirect_uri" to redirectUri
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

    suspend fun exchangeIdTokenWithSupabase(idToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
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
//            val result = exchangeGoogleIdTokenWithSupabase(idToken)
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
                //SeredPrefrence
                val sessionJson = Gson().toJson(session)
                val sharedPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                sharedPrefs.edit().putString("session", Gson().toJson(session)).apply()
                Result.success(session)
            } else {
                Result.failure(Exception("Supabase error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun launchAppleLoginWebView(
        activity: Activity,
        clientId: String,
        redirectUri: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val dialog = Dialog(activity)
        val webView = WebView(activity)
        webView.settings.javaScriptEnabled = true
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("appleid.apple.com")
            .appendPath("auth")
            .appendPath("authorize")
            .appendQueryParameter("response_type", "code id_token")
            .appendQueryParameter("response_mode", "form_post")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("scope", "name email")
            .build()
            .toString()

        println("Apple Auth URL: $authUrl")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith(redirectUri)) {
                    val uri = Uri.parse(url)
                    val code = uri.getQueryParameter("code")
                    val idToken = uri.getQueryParameter("id_token")
                    if (idToken != null) {
                        onSuccess(idToken)
                    } else if (code != null) {
                        onSuccess(code)
                    } else {
                        onError("No code or id_token found in redirect")
                    }
                    dialog.dismiss()
                    return true
                }
                return false
            }
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                println("WebView error: ${error?.description}")
            }
        }

        webView.loadUrl(authUrl)
        dialog.setContentView(webView)
        dialog.show()
    }

    fun launchAppleLoginInBrowser(
        activity: Activity,
        clientId: String,
        redirectUri: String
    ) {
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("appleid.apple.com")
            .appendPath("auth")
            .appendPath("authorize")
            .appendQueryParameter("response_type", "code id_token")
            .appendQueryParameter("response_mode", "form_post")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("scope", "name email")
            .build()
            .toString()

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        activity.startActivity(intent)
    }
}
//package lc.fungee.IngrediCheck.auth
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import net.openid.appauth.*
//import io.ktor.client.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.client.engine.okhttp.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import android.app.Dialog
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.net.Uri
//import android.annotation.SuppressLint
//import android.view.ViewGroup
//import android.webkit.WebResourceError
//import android.webkit.WebResourceRequest
//import kotlinx.serialization.json.JsonObject
//import lc.fungee.IngrediCheck.auth.SupabaseSession
//import lc.fungee.IngrediCheck.auth.SupabaseUser
//import com.google.gson.Gson
//
//class AppleAuthRepository(
//    private val context: Context,
//    private val supabaseUrl: String,
//    private val supabaseAnonKey: String
//) {
//    private val client = HttpClient(OkHttp) {
//        install(ContentNegotiation) {
//            json(Json { ignoreUnknownKeys = true })
//        }
//    }
//
//    private val authService by lazy { AuthorizationService(context) }
//
//    fun getAppleAuthRequest(): AuthorizationRequest {
//        val serviceConfig = AuthorizationServiceConfiguration(
//            android.net.Uri.parse("https://appleid.apple.com/auth/authorize"),
//            android.net.Uri.parse("https://appleid.apple.com/auth/token")
//        )
//        val redirectUri = android.net.Uri.parse("https://wqidjkpfdrvomfkmefqc.supabase.co/auth/v1/callback")
//        return AuthorizationRequest.Builder(
//            serviceConfig,
//            "llc.fungee.ingredicheck.web", // Service ID from Apple
//            ResponseTypeValues.CODE,
//            redirectUri
//        )
//            .setScopes("name", "email")
//            .setResponseMode("form_post")
//            .build()
//    }
//
//    fun performAuthRequest(activity: Activity, request: AuthorizationRequest) {
//        val intent = authService.getAuthorizationRequestIntent(request)
//        activity.startActivityForResult(intent, 1001)
//        // Handle the result in your Activity's onActivityResult or Activity Result API
//    }
//
//    suspend fun exchangeCodeWithSupabase(code: String, redirectUri: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
//        try {
//            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=pkce") {
//                headers.append("apikey", supabaseAnonKey)
//                headers.append("Content-Type", "application/json")
//                setBody(
//                    mapOf(
//                        "grant_type" to "authorization_code",
//                        "code" to code,
//                        "redirect_uri" to redirectUri
//                    )
//                )
//            }
//            if (response.status.value in 200..299) {
//                val responseBody = response.bodyAsText()
//                println("Supabase response: $responseBody")
//                val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
//                Result.success(session)
//            } else {
//                Result.failure(Exception("Supabase error: ${response.status}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun exchangeIdTokenWithSupabase(idToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
//        try {
//            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=id_token") {
//                headers.append("apikey", supabaseAnonKey)
//                headers.append("Content-Type", "application/json")
//                setBody(
//                    mapOf(
//                        "provider" to "apple",
//                        "id_token" to idToken
//                    )
//                )
//            }
//            if (response.status.value in 200..299) {
//                val responseBody = response.bodyAsText()
//                println("Supabase response: $responseBody")
//                val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
//                Result.success(session)
//            } else {
//                Result.failure(Exception("Supabase error: ${response.status}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun exchangeGoogleIdTokenWithSupabase(idToken: String): Result<SupabaseSession> = withContext(Dispatchers.IO) {
//        try {
////            val result = exchangeGoogleIdTokenWithSupabase(idToken)
//            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/token?grant_type=id_token") {
//                headers.append("apikey", supabaseAnonKey)
//                headers.append("Content-Type", "application/json")
//                setBody(
//                    mapOf(
//                        "provider" to "google",
//                        "id_token" to idToken
//                    )
//                )
//            }
//            if (response.status.value in 200..299) {
//                val responseBody = response.bodyAsText()
//                println("Supabase response: $responseBody")
//                val session = Gson().fromJson(responseBody, SupabaseSession::class.java)
//                Result.success(session)
//            } else {
//                Result.failure(Exception("Supabase error: ${response.status}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    fun launchAppleLoginWebView(
//        activity: Activity,
//        clientId: String,
//        redirectUri: String,
//        onSuccess: (String) -> Unit,
//        onError: (String) -> Unit
//    ) {
//        val dialog = Dialog(activity)
//        val webView = WebView(activity)
//        webView.settings.javaScriptEnabled = true
//        webView.layoutParams = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//
//        val authUrl = Uri.Builder()
//            .scheme("https")
//            .authority("appleid.apple.com")
//            .appendPath("auth")
//            .appendPath("authorize")
//            .appendQueryParameter("response_type", "code id_token")
//            .appendQueryParameter("response_mode", "form_post")
//            .appendQueryParameter("client_id", clientId)
//            .appendQueryParameter("redirect_uri", redirectUri)
//            .appendQueryParameter("scope", "name email")
//            .build()
//            .toString()
//
//        println("Apple Auth URL: $authUrl")
//
//        webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                if (url != null && url.startsWith(redirectUri)) {
//                    val uri = Uri.parse(url)
//                    val code = uri.getQueryParameter("code")
//                    val idToken = uri.getQueryParameter("id_token")
//                    if (idToken != null) {
//                        onSuccess(idToken)
//                    } else if (code != null) {
//                        onSuccess(code)
//                    } else {
//                        onError("No code or id_token found in redirect")
//                    }
//                    dialog.dismiss()
//                    return true
//                }
//                return false
//            }
//            override fun onReceivedError(
//                view: WebView?,
//                request: WebResourceRequest?,
//                error: WebResourceError?
//            ) {
//                super.onReceivedError(view, request, error)
//                println("WebView error: ${error?.description}")
//            }
//        }
//
//        webView.loadUrl(authUrl)
//        dialog.setContentView(webView)
//        dialog.show()
//    }
//
//    fun launchAppleLoginInBrowser(
//        activity: Activity,
//        clientId: String,
//        redirectUri: String
//    ) {
//        val authUrl = Uri.Builder()
//            .scheme("https")
//            .authority("appleid.apple.com")
//            .appendPath("auth")
//            .appendPath("authorize")
//            .appendQueryParameter("response_type", "code id_token")
//            .appendQueryParameter("response_mode", "form_post")
//            .appendQueryParameter("client_id", clientId)
//            .appendQueryParameter("redirect_uri", redirectUri)
//            .appendQueryParameter("scope", "name email")
//            .build()
//            .toString()
//
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
//        activity.startActivity(intent)
//    }
//}
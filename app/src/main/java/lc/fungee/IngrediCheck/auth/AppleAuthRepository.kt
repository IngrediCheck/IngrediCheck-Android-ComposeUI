
package lc.fungee.IngrediCheck.auth

// Android imports
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

// Kotlin coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Supabase SDK imports
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// Local model imports
import lc.fungee.IngrediCheck.data.model.SupabaseSession
import lc.fungee.IngrediCheck.data.model.Identity

// Local auth imports
import lc.fungee.IngrediCheck.auth.AppleLoginWebViewActivity
import lc.fungee.IngrediCheck.auth.SharedPreferencesSessionManager

// Supabase auth models
import io.github.jan.supabase.auth.user.UserSession as SdkUserSession
import kotlinx.serialization.decodeFromString

@OptIn(kotlin.time.ExperimentalTime::class)
class AppleAuthRepository(
    private val context: Context,
    private val supabaseUrl: String,
    private val supabaseAnonKey: String
) {
    @OptIn(SupabaseInternal::class)
    val supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseAnonKey
    ) {
        install(Auth) {
            sessionManager = SharedPreferencesSessionManager(context)
        }
        install(Postgrest)
        install(Storage)
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

    fun launchGoogleLoginWebView(activity: Activity) {
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("wqidjkpfdrvomfkmefqc.supabase.co")
            .appendPath("auth")
            .appendPath("v1")
            .appendPath("authorize")
            .appendQueryParameter("provider", "google")
            .appendQueryParameter("redirect_to", "io.supabase.ingredicheck://login-callback")
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(activity, authUrl)
    }

    // SDK-only approach: instruct caller to use SDK OAuth helpers instead of manual HTTP
    suspend fun exchangeAppleCodeWithSupabase(code: String): Result<io.github.jan.supabase.auth.user.UserSession> =
        Result.failure(UnsupportedOperationException("Use Supabase SDK OAuth flow to sign in; manual HTTP removed."))

    suspend fun exchangeAppleIdTokenWithSupabase(idToken: String): Result<io.github.jan.supabase.auth.user.UserSession> =
        Result.failure(UnsupportedOperationException("Use Supabase SDK OAuth flow to sign in; manual HTTP removed."))

    suspend fun signInWithGoogleIdTokenSdk(idToken: String): Result<io.github.jan.supabase.auth.user.UserSession> =
        Result.failure(UnsupportedOperationException("Use launchGoogleLoginWebView() for Google OAuth; ID token direct import not supported by current SDK."))

    // Anonymous Sign-In via SDK
    suspend fun signInAnonymously(): Result<io.github.jan.supabase.auth.user.UserSession> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AppleAuthRepository", "Starting anonymous sign-in")
            supabaseClient.auth.signInAnonymously()
            val session = supabaseClient.auth.currentSessionOrNull()
            if (session != null) {
                android.util.Log.d("AppleAuthRepository", "Anonymous sign-in successful")
                return@withContext Result.success(session)
            } else {
                android.util.Log.e("AppleAuthRepository", "Anonymous sign-in succeeded but no session found")
                return@withContext Result.failure(Exception("No session found after anonymous sign-in"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AppleAuthRepository", "Anonymous sign-in failed", e)
            return@withContext Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentSession(): io.github.jan.supabase.auth.user.UserSession? {
        return try {
            supabaseClient.auth.currentSessionOrNull()
        } catch (_: Exception) {
            null
        }
    }
}
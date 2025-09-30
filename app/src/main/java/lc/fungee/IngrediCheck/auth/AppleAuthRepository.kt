
package lc.fungee.IngrediCheck.auth

// Android imports
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
// Kotlin coroutines
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

// Supabase SDK imports
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// Local model imports
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
            sessionManager = SharedPreferencesSessionManager(context.applicationContext)
        }
        install(Postgrest)
        install(Storage)
    }

    fun hasStoredSession(): Boolean {
        return try {
            context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)
                .getString("session", null) != null
        } catch (_: Exception) {
            false
        }
    }

    // Import session from implicit flow tokens (access_token/refresh_token)
    suspend fun importSessionFromTokens(
        accessToken: String,
        refreshToken: String,
        expiresInSeconds: Long,
        tokenType: String
    ): Result<io.github.jan.supabase.auth.user.UserSession> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AppleAuthRepository", "Importing session from implicit tokens")
            val session = SdkUserSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = expiresInSeconds.toInt().toLong(),
                tokenType = tokenType,
                user = null
            )
            supabaseClient.auth.importSession(session)
            val current = supabaseClient.auth.currentSessionOrNull()
            if (current != null) {
                android.util.Log.d("AppleAuthRepository", "Session import successful")
                Result.success(current)
            } else {
                android.util.Log.e("AppleAuthRepository", "Session import completed but no session found")
                Result.failure(IllegalStateException("No session present after import"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AppleAuthRepository", "Session import failed", e)
            Result.failure(e)
        }
    }

    fun launchAppleLoginWebView(activity: Activity) {
        val appRedirect = "io.supabase.ingredicheck://callback"
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("wqidjkpfdrvomfkmefqc.supabase.co")
            .appendPath("auth")
            .appendPath("v1")
            .appendPath("authorize")
            .appendQueryParameter("provider", "apple")
            .appendQueryParameter("redirect_to", appRedirect)
            .build()
            .toString()

        val intent = Intent(activity, AppleLoginWebViewActivity::class.java).apply {
            putExtra("auth_url", authUrl)
            putExtra("redirect_uri", appRedirect)
        }
        activity.startActivityForResult(intent, 1002)
    }

    // Exchange OAuth code (from Supabase authorize redirect) for a local session via Supabase SDK
    suspend fun exchangeAppleCodeWithSupabase(code: String): Result<io.github.jan.supabase.auth.user.UserSession> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("AppleAuthRepository", "Exchanging code for session via Supabase")
                supabaseClient.auth.exchangeCodeForSession(code)
                val session = supabaseClient.auth.currentSessionOrNull()
                if (session != null) {
                    android.util.Log.d("AppleAuthRepository", "Code exchange successful; session present")
                    Result.success(session)
                } else {
                    android.util.Log.e("AppleAuthRepository", "Code exchange completed but no session found")
                    Result.failure(IllegalStateException("No session present after code exchange"))
                }
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthRepository", "Code exchange failed", e)
                Result.failure(e)
            }
        }

    // Apple: native ID token sign-in via supabase-kt
    suspend fun exchangeAppleIdTokenWithSupabase(idToken: String): Result<io.github.jan.supabase.auth.user.UserSession> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("AppleAuthRepository", "Signing in with Apple ID token via Supabase")
                supabaseClient.auth.signInWith(IDToken) {
                    this.idToken = idToken
                    provider = Apple
                }
                val status = supabaseClient.auth.sessionStatus.value
                android.util.Log.d("AppleAuthRepository", "Apple sign-in completed. sessionStatus=$status, hasSession=${supabaseClient.auth.currentSessionOrNull() != null}")
                val session = supabaseClient.auth.currentSessionOrNull()
                if (session != null) Result.success(session) else Result.failure(IllegalStateException("No session present after Apple sign-in"))
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthRepository", "Apple sign-in with ID token failed", e)
                Result.failure(e)
            }
        }

    // Google: native ID token sign-in via supabase-kt
    suspend fun signInWithGoogleIdTokenSdk(idToken: String): Result<io.github.jan.supabase.auth.user.UserSession> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("AppleAuthRepository", "Signing in with Google ID token via Supabase")
                supabaseClient.auth.signInWith(IDToken) {
                    this.idToken = idToken
                    provider = Google
                }
                val status = supabaseClient.auth.sessionStatus.value
                android.util.Log.d("AppleAuthRepository", "Google sign-in completed. sessionStatus=$status, hasSession=${supabaseClient.auth.currentSessionOrNull() != null}")
                val session = supabaseClient.auth.currentSessionOrNull()
                if (session != null) Result.success(session) else Result.failure(IllegalStateException("No session present after Google sign-in"))
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthRepository", "Google sign-in with ID token failed", e)
                Result.failure(e)
            }
        }

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
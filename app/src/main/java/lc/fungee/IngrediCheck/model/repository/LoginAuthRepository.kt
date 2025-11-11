package lc.fungee.IngrediCheck.model.repository
import lc.fungee.IngrediCheck.model.utils.AppConstants
import lc.fungee.IngrediCheck.model.entities.AppleAuthConfig

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.SupabaseClient

import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lc.fungee.IngrediCheck.ui.view.screens.onboarding.AppleLoginWebViewActivity
import lc.fungee.IngrediCheck.model.source.SharedPreferencesSessionManager
import kotlin.time.ExperimentalTime
import lc.fungee.IngrediCheck.model.repository.auth.AuthProvider
 

@OptIn(ExperimentalTime::class)
class LoginAuthRepository(
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
            context.getSharedPreferences(AppConstants.Prefs.SUPABASE_SESSION, Context.MODE_PRIVATE)
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
    ): Result<UserSession> = withContext(Dispatchers.IO) {
        try {
            Log.d("AppleAuthRepository", "Importing session from implicit tokens")
            val session = UserSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = expiresInSeconds.toInt().toLong(),
                tokenType = tokenType,
                user = null
            )
            supabaseClient.auth.importSession(session)
            val current = supabaseClient.auth.currentSessionOrNull()
            if (current != null) {
                Log.d("AppleAuthRepository", "Session import successful")
                Result.success(current)
            } else {
                Log.e("AppleAuthRepository", "Session import completed but no session found")
                Result.failure(IllegalStateException("No session present after import"))
            }
        } catch (e: Exception) {
            Log.e("AppleAuthRepository", "Session import failed", e)
            Result.failure(e)
        }
    }

    fun launchAppleLoginWebView(activity: Activity) {
        val appRedirect = "${AppleAuthConfig.APP_SCHEME}://callback"
        val authUrl = Uri.Builder()
            .scheme("https")
            .authority(Uri.parse(supabaseUrl).host ?: AppConstants.Supabase.HOST)
            .appendPath("auth")
            .appendPath("v1")
            .appendPath("authorize")
            .appendQueryParameter("provider", AppConstants.Providers.APPLE)
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
    suspend fun exchangeAppleCodeWithSupabase(code: String): Result<UserSession> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("AppleAuthRepository", "Exchanging code for session via Supabase")
                supabaseClient.auth.exchangeCodeForSession(code)
                val session = supabaseClient.auth.currentSessionOrNull()
                if (session != null) {
                    Log.d("AppleAuthRepository", "Code exchange successful; session present")
                    Result.success(session)
                } else {
                    Log.e("AppleAuthRepository", "Code exchange completed but no session found")
                    Result.failure(IllegalStateException("No session present after code exchange"))
                }
            } catch (e: Exception) {
                Log.e("AppleAuthRepository", "Code exchange failed", e)
                Result.failure(e)
            }
        }

    // Apple: native ID token sign-in via supabase-kt
    suspend fun exchangeAppleIdTokenWithSupabase(idToken: String): Result<UserSession> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("AppleAuthRepository", "Signing in with Apple ID token via Supabase")
                supabaseClient.auth.signInWith(IDToken) {
                    this.idToken = idToken
                    provider = Apple
                }
                val status = supabaseClient.auth.sessionStatus.value
                Log.d(
                    "AppleAuthRepository",
                    "Apple sign-in completed. sessionStatus=$status, hasSession=${supabaseClient.auth.currentSessionOrNull() != null}"
                )
                val session = supabaseClient.auth.currentSessionOrNull()
                if (session != null) Result.success(session) else Result.failure(
                    IllegalStateException("No session present after Apple sign-in")
                )
            } catch (e: Exception) {
                Log.e("AppleAuthRepository", "Apple sign-in with ID token failed", e)
                Result.failure(e)
            }
        }

    // Google: native ID token sign-in via supabase-kt
    suspend fun signInWithGoogleIdTokenSdk(idToken: String): Result<UserSession> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("AppleAuthRepository", "Signing in with Google ID token via Supabase")
                supabaseClient.auth.signInWith(IDToken) {
                    this.idToken = idToken
                    provider = Google
                }
                val status = supabaseClient.auth.sessionStatus.value
                Log.d(
                    "AppleAuthRepository",
                    "Google sign-in completed. sessionStatus=$status, hasSession=${supabaseClient.auth.currentSessionOrNull() != null}"
                )
                val session = supabaseClient.auth.currentSessionOrNull()
                if (session != null) Result.success(session) else Result.failure(
                    IllegalStateException("No session present after Google sign-in")
                )
            } catch (e: Exception) {
                Log.e("AppleAuthRepository", "Google sign-in with ID token failed", e)
                Result.failure(e)
            }
        }

    // Anonymous Sign-In via SDK
    suspend fun signInAnonymously(): Result<UserSession> = withContext(Dispatchers.IO) {
        try {
            Log.d("AppleAuthRepository", "Starting anonymous sign-in")
            supabaseClient.auth.signInAnonymously()
            val session = supabaseClient.auth.currentSessionOrNull()
            if (session != null) {
                Log.d("AppleAuthRepository", "Anonymous sign-in successful")
                return@withContext Result.success(session)
            } else {
                Log.e("AppleAuthRepository", "Anonymous sign-in succeeded but no session found")
                return@withContext Result.failure(Exception("No session found after anonymous sign-in"))
            }
        } catch (e: Exception) {
            Log.e("AppleAuthRepository", "Anonymous sign-in failed", e)
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

    fun getCurrentSession(): UserSession? {
        return try {
            supabaseClient.auth.currentSessionOrNull()
        } catch (_: Exception) {
            null
        }
    }

    fun currentSessionOrNull(): UserSession? = getCurrentSession()

    suspend fun clearLocalData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE).edit().clear()
                .apply()
            context.getSharedPreferences(AppConstants.Prefs.SUPABASE_SESSION, Context.MODE_PRIVATE).edit().clear()
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccountAndData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            runCatching { supabaseClient.auth.signOut() }
            context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE).edit().clear()
                .apply()
            context.getSharedPreferences(AppConstants.Prefs.SUPABASE_SESSION, Context.MODE_PRIVATE).edit().clear()
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun authProvider(): AuthProvider {
        return try {
            val provider = context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                .getString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, null)
            when (provider) {
                AppConstants.Providers.APPLE -> AuthProvider.APPLE
                AppConstants.Providers.GOOGLE -> AuthProvider.GOOGLE
                AppConstants.Providers.ANONYMOUS -> AuthProvider.ANONYMOUS
                else -> AuthProvider.NONE
            }
        } catch (_: Exception) {
            AuthProvider.NONE
        }
    }

    suspend fun signInWithGoogleIdToken(idToken: String) = signInWithGoogleIdTokenSdk(idToken)
    suspend fun signInWithAppleIdToken(idToken: String) = exchangeAppleIdTokenWithSupabase(idToken)
}

package lc.fungee.Ingredicheck.auth

import android.content.Context
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository(
    context: Context
) {
    val supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = AuthEnv.SUPABASE_URL,
        supabaseKey = AuthEnv.SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            sessionManager = SharedPreferencesSessionManager(context.applicationContext)
        }
        install(Postgrest)
        install(Storage)
    }

    suspend fun currentSessionOrNull(): UserSession? = withContext(Dispatchers.IO) {
        supabaseClient.auth.currentSessionOrNull()
    }

    val sessionFlow: Flow<UserSession?> = supabaseClient.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> status.session
            else -> null
        }
    }

    suspend fun accessTokenOrNull(): String? = withContext(Dispatchers.IO) {
        supabaseClient.auth.currentSessionOrNull()?.accessToken
    }

    suspend fun signInAnonymously(): Result<UserSession> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.auth.signInAnonymously()
            supabaseClient.auth.currentSessionOrNull() ?: error("No session present after anonymous sign-in")
        }
    }

    suspend fun signInWithGoogleIdToken(idToken: String): Result<UserSession> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }
            supabaseClient.auth.currentSessionOrNull() ?: error("No session present after Google sign-in")
        }
    }

    suspend fun exchangeAppleCodeForSession(code: String): Result<UserSession> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.auth.exchangeCodeForSession(code)
            supabaseClient.auth.currentSessionOrNull() ?: error("No session present after Apple code exchange")
        }
    }

    suspend fun signInWithAppleIdToken(idToken: String): Result<UserSession> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Apple
            }
            supabaseClient.auth.currentSessionOrNull() ?: error("No session present after Apple sign-in")
        }
    }

    suspend fun syncOnboardingMetadata(step: String, stage: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            // Only attempt if there is an active session (guest or logged in)
            val session = supabaseClient.auth.currentSessionOrNull() ?: run {
                Log.d("AuthRepository", "syncOnboardingMetadata skipped: no active session")
                return@runCatching
            }

            Log.d(
                "AuthRepository",
                "syncOnboardingMetadata started userId=${session.user?.id}, step=$step, stage=$stage"
            )

            try {
                supabaseClient.auth.updateUser {
                    data {
                        put("onboarding_step", step)
                        put("onboarding_stage", stage)
                    }
                }
                Log.d("AuthRepository", "syncOnboardingMetadata success userId=${session.user?.id}")
            } catch (e: Exception) {
                Log.e("AuthRepository", "syncOnboardingMetadata failed userId=${session.user?.id}", e)
                throw e
            }
        }
    }
}

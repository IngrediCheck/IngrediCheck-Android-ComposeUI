package lc.fungee.IngrediCheck.debug

import android.content.Context
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession

@OptIn(kotlin.time.ExperimentalTime::class)
object SessionDebugHelper {
    
    fun logSessionInfo(session: UserSession?, tag: String = "SessionDebug") {
        if (session != null) {
            Log.d(tag, "=== SESSION INFO ===")
            Log.d(tag, "User ID: ${session.user?.id}")
            Log.d(tag, "User Email: ${session.user?.email}")
            Log.d(tag, "Token Type: ${session.tokenType}")
            Log.d(tag, "Expires In: ${session.expiresIn}")
            Log.d(tag, "Expires At: ${session.expiresAt}")
            Log.d(tag, "Access Token: ${session.accessToken.take(20)}...")
            Log.d(tag, "Refresh Token: ${session.refreshToken.take(20)}...")
            // Anonymous flag may not be available on all SDK versions
            Log.d(tag, "==================")
        } else {
            Log.d(tag, "No session found")
        }
    }
    
    fun logSupabaseClientInfo(supabaseClient: SupabaseClient, tag: String = "SessionDebug") {
        try {
            val session = supabaseClient.auth.currentSessionOrNull()
            logSessionInfo(session, tag)
        } catch (e: Exception) {
            Log.e(tag, "Error getting session from Supabase client", e)
        }
    }
    
    fun logSharedPreferencesInfo(context: Context, tag: String = "SessionDebug") {
        try {
            val prefs = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)
            val sessionJson = prefs.getString("session", null)
            Log.d(tag, "=== SHARED PREFERENCES INFO ===")
            Log.d(tag, "Supabase session exists: ${sessionJson != null}")
            if (sessionJson != null) {
                Log.d(tag, "Session JSON length: ${sessionJson.length}")
                Log.d(tag, "Session JSON preview: ${sessionJson.take(100)}...")
            }
            
            val userPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val userSessionJson = userPrefs.getString("session", null)
            Log.d(tag, "User session exists: ${userSessionJson != null}")
            if (userSessionJson != null) {
                Log.d(tag, "User session JSON length: ${userSessionJson.length}")
                Log.d(tag, "User session JSON preview: ${userSessionJson.take(100)}...")
            }
            Log.d(tag, "=============================")
        } catch (e: Exception) {
            Log.e(tag, "Error reading SharedPreferences", e)
        }
    }
    
    fun clearAllSessions(context: Context, tag: String = "SessionDebug") {
        try {
            Log.d(tag, "Clearing all sessions...")
            context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
            Log.d(tag, "All sessions cleared")
        } catch (e: Exception) {
            Log.e(tag, "Error clearing sessions", e)
        }
    }
    
    fun testSessionFlow(supabaseClient: SupabaseClient, context: Context, tag: String = "SessionDebug") {
        try {
            Log.d(tag, "=== TESTING SESSION FLOW ===")
            
            // Test 1: Check current session
            val currentSession = supabaseClient.auth.currentSessionOrNull()
            Log.d(tag, "Test 1 - Current session: ${currentSession != null}")
            if (currentSession != null) {
                Log.d(tag, "Test 1 - User email: ${currentSession.user?.email}")
                Log.d(tag, "Test 1 - Token expires at: ${currentSession.expiresAt}")
            }
            
            // Test 2: Check SharedPreferences
            val supabasePrefs = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)
            val supabaseSessionExists = supabasePrefs.getString("session", null) != null
            Log.d(tag, "Test 2 - Supabase session in prefs: $supabaseSessionExists")
            
            val userPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val userSessionExists = userPrefs.getString("session", null) != null
            Log.d(tag, "Test 2 - User session in prefs: $userSessionExists")
            
            // Test 3: Check token validity
            if (currentSession != null) {
                val now = System.currentTimeMillis() // Current time in milliseconds
                val expiresAt = currentSession.expiresAt
                val isExpired = if (expiresAt != null) {
                    // Convert expiresAt to milliseconds if it's not already
                    val expiresAtMillis = when (expiresAt) {
                        is Long -> expiresAt
                        is kotlin.time.Instant -> expiresAt.toEpochMilliseconds()
                        else -> {
                            Log.w(tag, "Unknown expiresAt type: ${expiresAt::class.simpleName}")
                            0L
                        }
                    }
                    now >= expiresAtMillis
                } else {
                    Log.w(tag, "expiresAt is null, cannot determine if expired")
                    false
                }
                Log.d(tag, "Test 3 - Token expired: $isExpired")
                Log.d(tag, "Test 3 - Current time: $now")
                Log.d(tag, "Test 3 - Expires at: $expiresAt")
            }
            
            Log.d(tag, "=== SESSION FLOW TEST COMPLETE ===")
        } catch (e: Exception) {
            Log.e(tag, "Error testing session flow", e)
        }
    }
}

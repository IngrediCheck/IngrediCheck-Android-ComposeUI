package lc.fungee.IngrediCheck.model.utils

import android.net.Uri
import lc.fungee.IngrediCheck.model.AuthEnv

/**
 * Centralized constants and helpers used across the app.
 * - API endpoints (Edge Functions base)
 * - Supabase config accessors
 * - SharedPreferences keys and provider IDs
 * - Public website links
 */
object AppConstants {

    object Website {
        const val BASE = "https://www.ingredicheck.app"
        const val ABOUT = "$BASE/about"
        const val TERMS = "$BASE/terms-conditions"
        const val PRIVACY = "$BASE/privacy-policy"
    }

    object Prefs {
        // SharedPreferences file names
        const val USER_SESSION = "user_session"
        const val SUPABASE_SESSION = "supabase_session"
        const val INTERNAL_FLAGS = "internal_flags"

        // Common keys inside SharedPreferences
        const val KEY_LOGIN_PROVIDER = "login_provider"
        const val KEY_DISCLAIMER_ACCEPTED = "disclaimer_accepted"
        const val KEY_INTERNAL_MODE = "is_internal_user"
    }

    object Providers {
        const val APPLE = "apple"
        const val GOOGLE = "google"
        const val ANONYMOUS = "anonymous"
    }

    object Functions {
        const val INGREDICHECK_PATH = "/functions/v1/ingredicheck"

        fun baseFrom(url: String): String = url.trimEnd('/') + INGREDICHECK_PATH

        // Convenience accessor using AuthEnv
        val base: String
            get() = baseFrom(Supabase.URL)
    }

    object Supabase {
        val URL: String
            get() = AuthEnv.SUPABASE_URL

        val ANON_KEY: String
            get() = AuthEnv.SUPABASE_ANON_KEY

        val HOST: String?
            get() = Uri.parse(URL).host
    }

    fun isInternalEnabled(context: android.content.Context): Boolean {
        return try {
            context.getSharedPreferences(Prefs.INTERNAL_FLAGS, android.content.Context.MODE_PRIVATE)
                .getBoolean(Prefs.KEY_INTERNAL_MODE, false)
        } catch (_: Exception) { false }
    }

    fun setInternalEnabled(context: android.content.Context, enabled: Boolean) {
        try {
            context.getSharedPreferences(Prefs.INTERNAL_FLAGS, android.content.Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Prefs.KEY_INTERNAL_MODE, enabled)
                .apply()
        } catch (_: Exception) { }
    }
}


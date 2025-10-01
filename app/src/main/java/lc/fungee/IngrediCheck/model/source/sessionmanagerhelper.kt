package lc.fungee.IngrediCheck.model.source

import android.content.Context
import android.content.SharedPreferences
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SharedPreferencesSessionManager(
    private val context: Context
) : SessionManager {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)

    override suspend fun saveSession(session: UserSession) {
        withContext(Dispatchers.IO) {
            val jsonSession = Json.Default.encodeToString(session)
            prefs.edit().putString("session", jsonSession).apply()
        }
    }

    override suspend fun loadSession(): UserSession? {
        return withContext(Dispatchers.IO) {
            prefs.getString("session", null)?.let {
                Json.Default.decodeFromString<UserSession>(it)
            }
        }
    }

    override suspend fun deleteSession() {
        withContext(Dispatchers.IO) {
            prefs.edit().remove("session").apply()
        }
    }
}
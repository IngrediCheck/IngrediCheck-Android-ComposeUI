package lc.fungee.IngrediCheck.data.source

import android.content.Context
import com.google.gson.Gson
import lc.fungee.IngrediCheck.auth.SupabaseSession

class SessionLocalDataSource(private val context: Context) {
    fun getSession(): SupabaseSession? {
        val json = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            .getString("session", null)
        return json?.let { Gson().fromJson(it, SupabaseSession::class.java) }
    }
    fun getUserId(): String? = getSession()?.user?.id
}
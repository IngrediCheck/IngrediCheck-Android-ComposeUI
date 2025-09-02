

package lc.fungee.IngrediCheck.data.repository

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import lc.fungee.IngrediCheck.data.model.DietaryPreference
import lc.fungee.IngrediCheck.data.model.NewDietaryPreference
import lc.fungee.IngrediCheck.data.source.SessionLocalDataSource

class PreferenceRepositoryImpl(
    context: Context,
    supabaseUrl: String,
    supabaseAnonKey: String
) : PreferenceRepository {

    private val sessionLocal = SessionLocalDataSource(context)

    private val supabase: SupabaseClient = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseAnonKey
    ) {
        install(Postgrest)
    }

    override suspend fun getPreferencesForUser(userId: String): List<DietaryPreference> {
        return supabase.from("preferences")
            .select {
                filter {
                    eq("user_id", userId)
                }
                order("created_at", order = Order.DESCENDING)
            }
            .decodeList<DietaryPreference>()
    }

    override suspend fun savePreference(userId: String, text: String): DietaryPreference {
        return supabase.from("preferences")
            .insert(NewDietaryPreference(userId = userId, preferenceText = text)) {
                select() // return inserted row
            }
            .decodeSingle<DietaryPreference>()
    }
}
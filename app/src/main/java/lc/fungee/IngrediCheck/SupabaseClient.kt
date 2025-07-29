import com.google.android.gms.auth.api.Auth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue


import io.github.jan.supabase.postgrest.Postgrest

object SupabaseManager {
    private const val SUPABASE_URL = "https://wqidjkpfdrvomfkmefqc.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI"
    const val SUPABASE_FUNCTIONS_URL = "${SUPABASE_URL}/functions/v1/ingredicheck"
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY,

        ) {

            install(GoTrue) {
                scheme = "llc.fungee.IngrediCheck"
                host = "supabase.co"
            }

            install(Postgrest)

        }
    }
}
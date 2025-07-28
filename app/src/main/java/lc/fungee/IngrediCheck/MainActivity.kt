
package lc.fungee.IngrediCheck
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lc.fungee.IngrediCheck.ui.theme.IngrediCheckTheme
import lc.fungee.IngrediCheck.auth.AppleAuthRepository
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.auth.GoogleAuthClient
import lc.fungee.IngrediCheck.auth.rememberGoogleSignInLauncher
import androidx.compose.runtime.remember


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IngrediCheckTheme {
                val context = this
                val repository = remember {
                    AppleAuthRepository(
                        context = context,
                        supabaseUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co",
                        supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI"
                    )
                }

                val viewModel = remember { AppleAuthViewModel(repository) }
                val googleSignInClient = GoogleAuthClient.getClient(context)
                val googleSignInLauncher = rememberGoogleSignInLauncher( viewModel)

                AppNavigation(
                    viewModel = viewModel,
                    googleSignInLauncher = googleSignInLauncher,
                    googleSignInClient = googleSignInClient
                )
            }
        }
    }
}

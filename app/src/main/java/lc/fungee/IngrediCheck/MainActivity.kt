

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
import android.content.Intent
import android.util.Log
import lc.fungee.IngrediCheck.auth.AppleLoginState

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AppleAuthViewModel

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

                viewModel = remember { AppleAuthViewModel(repository) }
                val googleSignInClient = GoogleAuthClient.getClient(context)
                val googleSignInLauncher = rememberGoogleSignInLauncher(this, viewModel)

                AppNavigation(
                    viewModel = viewModel,
                    googleSignInLauncher = googleSignInLauncher,
                    googleSignInClient = googleSignInClient
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1002 && resultCode == RESULT_OK) {
            data?.let { intent ->
                val idToken = intent.getStringExtra("id_token")
                val code = intent.getStringExtra("code")

                if (idToken != null) {
                    viewModel.signInWithAppleIdToken(idToken)
                } else if (code != null) {
                    viewModel.signInWithAppleCode(code)
                } else {
                    viewModel.setError("No token or code received from Apple login")
                }
            }
        }
    }
}

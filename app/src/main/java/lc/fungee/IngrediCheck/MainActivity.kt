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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AppleAuthViewModel
    private lateinit var repository: AppleAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IngrediCheckTheme {
                val context = this
                repository = remember {
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

        android.util.Log.d("MainActivity", "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")

        if (requestCode == 1002 && resultCode == RESULT_OK && data != null) {
            android.util.Log.d("MainActivity", "Apple WebView result received")
            
            val accessToken = data.getStringExtra("access_token")
            val refreshToken = data.getStringExtra("refresh_token")
            val expiresIn = data.getIntExtra("expires_in", 3600)
            val idToken = data.getStringExtra("id_token")
            val code = data.getStringExtra("code")
            val error = data.getStringExtra("error")
            val errorDescription = data.getStringExtra("error_description")

            android.util.Log.d("MainActivity", "Apple result - accessToken: ${accessToken?.take(10)}..., refreshToken: ${refreshToken?.take(10)}..., expiresIn: $expiresIn")
            android.util.Log.d("MainActivity", "Apple result - idToken: ${idToken?.take(10)}..., code: ${code?.take(10)}...")
            android.util.Log.d("MainActivity", "Error: $error, Description: $errorDescription")

            if (error != null) {
                android.util.Log.e("MainActivity", "Apple login error: $error - $errorDescription")
                viewModel.setError("Apple login failed: $error")
            } else if (!refreshToken.isNullOrBlank()) {
                // ✅ Handle refresh token flow
                android.util.Log.d("MainActivity", "Processing refresh token flow")
                lifecycleScope.launch {
                    val result = repository.exchangeAppleRefreshTokenWithSupabase(refreshToken)
                    result.fold(
                        onSuccess = { session ->
                            android.util.Log.d("MainActivity", "Refresh token exchange successful")
                            try {
                                // Store session in SharedPreferences
                                viewModel.storeSession(session, this@MainActivity)
                                // Navigate to Disclaimer screen
                                navigateToDisclaimerScreen()
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "Error storing session", e)
                                viewModel.setError("Failed to store session: ${e.message}")
                            }
                        },
                        onFailure = { error ->
                            android.util.Log.e("MainActivity", "Failed to fetch Supabase session", error)
                            viewModel.setError("Failed to authenticate with Supabase: ${error.message}")
                        }
                    )
                }
            } else if (idToken != null) {
                android.util.Log.d("MainActivity", "Calling signInWithAppleIdToken")
                viewModel.signInWithAppleIdToken(idToken, this)
            } else if (code != null) {
                android.util.Log.d("MainActivity", "Calling signInWithAppleCode")
                viewModel.signInWithAppleCode(code, this)
            } else if (accessToken != null) {
                android.util.Log.d("MainActivity", "Calling signInWithAppleAccessToken")
                viewModel.signInWithAppleAccessToken(accessToken, this)
            } else {
                android.util.Log.e("MainActivity", "No token, code, or access_token received from Apple login")
                viewModel.setError("No authentication data received from Apple login")
            }
        } else if (requestCode == 1002) {
            android.util.Log.e("MainActivity", "Apple WebView failed with resultCode: $resultCode")
            viewModel.setError("Apple login was cancelled or failed")
        }
    }

    // ✅ Navigate to Disclaimer Screen
    private fun navigateToDisclaimerScreen() {
        android.util.Log.d("MainActivity", "Navigating to Disclaimer screen")
        // Since you're using Compose Navigation, we need to trigger navigation through the ViewModel
        viewModel.navigateToDisclaimer()
    }
}
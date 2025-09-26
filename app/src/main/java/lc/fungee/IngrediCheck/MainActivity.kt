package lc.fungee.IngrediCheck
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lc.fungee.IngrediCheck.ui.theme.IngrediCheckTheme
import lc.fungee.IngrediCheck.auth.AppleAuthRepository
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.auth.AppleLoginState
import lc.fungee.IngrediCheck.auth.GoogleAuthClient
import lc.fungee.IngrediCheck.auth.rememberGoogleSignInLauncher
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import lc.fungee.IngrediCheck.data.repository.PreferenceViewModel
import lc.fungee.IngrediCheck.navigation.AppNavigation
import lc.fungee.IngrediCheck.ui.screens.home.ErrorScreen
//import lc.fungee.IngrediCheck.ui.screens.home.LoadingScreen
import androidx.compose.runtime.CompositionLocalProvider


import io.github.jan.supabase.auth.auth
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect

import lc.fungee.IngrediCheck.navigation.NetworkViewmodel

class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AppleAuthViewModel
    private lateinit var repository: AppleAuthRepository
    private var preferenceViewModel: PreferenceViewModel? = null


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val supabaseUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co"
        val supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI" // shortened for clarity

        // Initialize repository and ViewModel before composing UI so deep links can be handled immediately
        repository = AppleAuthRepository(
            context = this@MainActivity,
            supabaseUrl = supabaseUrl,
            supabaseAnonKey = supabaseAnonKey
        )
        authViewModel = AppleAuthViewModel(repository)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(activity = this)
            IngrediCheckTheme {
                val networkViewModel: NetworkViewmodel = viewModel()
                val context = LocalContext.current
                LaunchedEffect(context) {
                    networkViewModel.startMonitoring(context.applicationContext)
                }


                // repository and authViewModel are already initialized before setContent
                // Collect the login state
                val loginState = authViewModel.loginState.collectAsState()
                val currentLoginState = loginState.value

                // Create PreferenceViewModel when authenticated, navigating to disclaimer, or when a stored session exists
                val currentPreferenceViewModel = remember(currentLoginState) {
                    val hasSdkSession = runCatching { repository.supabaseClient.auth.currentSessionOrNull() != null }.getOrDefault(false)
                    val hasStoredSession = context.getSharedPreferences("user_session",
                        android.content.Context.MODE_PRIVATE
                    )
                        .getString("session", null) != null

                    if (preferenceViewModel == null &&
                        (currentLoginState is AppleLoginState.Success ||
                         currentLoginState is AppleLoginState.NavigateToDisclaimer ||
                         hasSdkSession || hasStoredSession)
                    ) {
                        val preferenceRepository = PreferenceRepository(
							context = context,
							supabaseClient = repository.supabaseClient,
							functionsBaseUrl = "$supabaseUrl/functions/v1/ingredicheck",
							anonKey = supabaseAnonKey
                        )
                        preferenceViewModel = PreferenceViewModel(
                            preferenceRepository
                        )
                    }
                    preferenceViewModel
                }

                val googleSignInClient = GoogleAuthClient.getClient(this@MainActivity)
                val googleSignInLauncher = rememberGoogleSignInLauncher(this@MainActivity, authViewModel)

                when (val state = currentLoginState) {
                    is AppleLoginState.Loading -> {
                        //                            LoadingScreen()
                    }
                    is AppleLoginState.Success, is AppleLoginState.NavigateToDisclaimer -> {
                        // Only pass if not null
                        if (currentPreferenceViewModel != null) {
                            AppNavigation(
                                viewModel = authViewModel,
                                googleSignInLauncher = googleSignInLauncher,
                                googleSignInClient = googleSignInClient,
                                preferenceViewModel = currentPreferenceViewModel,
                                supabaseClient = repository.supabaseClient,
                                windowSize = windowSizeClass,  // âœ… Fixed: proper parameter name
                                isOnline = networkViewModel.isOnline,
                                functionsBaseUrl = "$supabaseUrl/functions/v1/ingredicheck",
                                anonKey = supabaseAnonKey
                            )
                        } else {
//                            LoadingScreen() // or show a fallback/loading
                        }
                    }
                    is AppleLoginState.Idle -> {
                        AppNavigation(
                            viewModel = authViewModel,
                            googleSignInLauncher = googleSignInLauncher,
                            googleSignInClient = googleSignInClient,
                            preferenceViewModel = currentPreferenceViewModel,
                            supabaseClient = repository.supabaseClient,
                            windowSize = windowSizeClass,
                            isOnline = networkViewModel.isOnline,
                            functionsBaseUrl = "$supabaseUrl/functions/v1/ingredicheck",
                            anonKey = supabaseAnonKey
                        )
                    }
                    is AppleLoginState.Error -> {
                        ErrorScreen(
                            message = state.message,
                            onRetry = { authViewModel.resetState() }
                        )
                    }
                }
            }
        }

        // Handle initial deep link if the activity was launched by OAuth redirect
        intent?.data?.let { processDeepLink(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1002 && resultCode == RESULT_OK && data != null) {
            val accessToken = data.getStringExtra("access_token")
            val refreshToken = data.getStringExtra("refresh_token")
            val idToken = data.getStringExtra("id_token")
            val code = data.getStringExtra("code")
            val error = data.getStringExtra("error")
            val errorDescription = data.getStringExtra("error_description")

            if (error != null) {
                authViewModel.setError("Apple login failed: $error")
            } else if (!refreshToken.isNullOrBlank()) {
                // Refresh token is handled automatically by Supabase SDK
                // No need for manual refresh token exchange
                authViewModel.setError("Refresh token handling is now automatic via Supabase SDK")
            } else if (idToken != null) {
                authViewModel.signInWithAppleIdToken(idToken, this)
            } else if (code != null) {
                authViewModel.signInWithAppleCode(code, this)
            } else if (accessToken != null) {
                // Do not accept raw Apple access tokens for Supabase auth
                authViewModel.setError("Apple id_token or code required for Supabase auth")
            } else {
                authViewModel.setError("No authentication data received from Apple login")
            }
        } else if (requestCode == 1002) {
            authViewModel.setError("Apple login was cancelled or failed")
        }
    }

    private fun navigateToDisclaimerScreen() {
        authViewModel.navigateToDisclaimer()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { processDeepLink(it) }
    }

    private fun processDeepLink(data: Uri) {
        android.util.Log.d("MainActivity", "Processing deep link: $data")
        try {
            // Try to call exchangeCodeForSession(Uri) if available in the SDK
            runCatching {
                val method = repository.supabaseClient.auth::class.java.methods.firstOrNull { m ->
                    m.name == "exchangeCodeForSession" && m.parameterTypes.size == 1
                }
                method?.invoke(repository.supabaseClient.auth, data)
            }.onFailure { ex ->
                android.util.Log.w("MainActivity", "exchangeCodeForSession not available or failed", ex)
            }

            val session = repository.supabaseClient.auth.currentSessionOrNull()
            if (session != null) {
                android.util.Log.d("MainActivity", "Supabase session restored from deep link")
                getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putString("login_provider", "google")
                    .apply()
                authViewModel.navigateToDisclaimer()
            } else {
                android.util.Log.e("MainActivity", "Deep link handled but no session present")
                authViewModel.setError("Login failed: No session present after redirect")
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error handling deep link", e)
            authViewModel.setError("Login failed: ${e.message}")
        }
    }
}

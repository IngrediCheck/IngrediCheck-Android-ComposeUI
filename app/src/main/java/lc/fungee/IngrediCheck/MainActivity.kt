package lc.fungee.IngrediCheck
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lc.fungee.IngrediCheck.ui.theme.IngrediCheckTheme
import lc.fungee.IngrediCheck.model.repository.LoginAuthRepository
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
import lc.fungee.IngrediCheck.viewmodel.AppleLoginState
import lc.fungee.IngrediCheck.viewmodel.LoginAuthViewModelFactory
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import lc.fungee.IngrediCheck.model.repository.PreferenceRepository
import lc.fungee.IngrediCheck.viewmodel.PreferenceViewModel
import lc.fungee.IngrediCheck.ui.view.navigation.AppNavigation
import lc.fungee.IngrediCheck.ui.view.screens.home.ErrorScreen
//import lc.fungee.IngrediCheck.ui.screens.home.LoadingScreen


import io.github.jan.supabase.auth.auth
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

import androidx.compose.runtime.LaunchedEffect

import lc.fungee.IngrediCheck.viewmodel.NetworkViewmodel
import lc.fungee.IngrediCheck.model.source.GoogleAuthDataSource
import lc.fungee.IngrediCheck.model.source.rememberGoogleSignInLauncher
import lc.fungee.IngrediCheck.model.entities.AppleAuthConfig
import lc.fungee.IngrediCheck.model.utils.AppConstants

class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AppleAuthViewModel
    private lateinit var repository: LoginAuthRepository
    private var preferenceViewModel: PreferenceViewModel? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val supabaseUrl = AppConstants.Supabase.URL
        val supabaseAnonKey = AppConstants.Supabase.ANON_KEY

        // Initialize repository and ViewModel with a Factory at Activity scope
        repository = LoginAuthRepository(
            context = this@MainActivity,
            supabaseUrl = supabaseUrl,
            supabaseAnonKey = supabaseAnonKey
        )
        val vmFactory = LoginAuthViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, vmFactory)
            .get(AppleAuthViewModel::class.java)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(activity = this)
            IngrediCheckTheme {
                val networkViewModel: NetworkViewmodel = viewModel()
                val context = LocalContext.current
                LaunchedEffect(context) {
                    networkViewModel.startMonitoring(context.applicationContext)
                }

                val googleSignInClient = GoogleAuthDataSource.getClient(this@MainActivity)
                val googleSignInLauncher = rememberGoogleSignInLauncher(this@MainActivity, authViewModel)

                val loginState = authViewModel.loginState.collectAsState()
                val currentLoginState = loginState.value

                // Create PreferenceViewModel when authenticated, navigating to disclaimer, or when a stored session exists
                val currentPreferenceViewModel = remember(currentLoginState) {
                    val hasSdkSession = runCatching { repository.supabaseClient.auth.currentSessionOrNull() != null }.getOrDefault(false)
                    // Read the same prefs used by SharedPreferencesSessionManager (supabase_session)
                    val hasStoredSession = context.getSharedPreferences(AppConstants.Prefs.SUPABASE_SESSION,
                        MODE_PRIVATE
                    ).getString("session", null) != null

                    if (preferenceViewModel == null &&
                        (currentLoginState is AppleLoginState.Success ||
                         currentLoginState is AppleLoginState.NavigateToDisclaimer ||
                         hasSdkSession || hasStoredSession)
                    ) {
                        val preferenceRepository = PreferenceRepository(
                            context = context,
                            supabaseClient = repository.supabaseClient,
                            functionsBaseUrl = AppConstants.Functions.base,
                            anonKey = AppConstants.Supabase.ANON_KEY
                        )
                        preferenceViewModel = PreferenceViewModel(
                            preferenceRepository
                        )
                    }
                    preferenceViewModel
                }

                when (val state = currentLoginState) {
                    is AppleLoginState.Loading -> {
                        //                            LoadingScreen() // or show a fallback/loading
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
                                windowSize = windowSizeClass,
                                isOnline = networkViewModel.isOnline,
                                functionsBaseUrl = AppConstants.Functions.base,
                                anonKey = AppConstants.Supabase.ANON_KEY
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
                            functionsBaseUrl = AppConstants.Functions.base,
                            anonKey = AppConstants.Supabase.ANON_KEY
                        )
                    }
                    is AppleLoginState.Error -> {
                        ErrorScreen(
                            message = state.message,
                            onRetry = { authViewModel.resetState() }
                        )
                    }
                }

                // Handle initial deep link (e.g., from Apple HTTPS bounce)
                LaunchedEffect(Unit) {
                    handleAppleDeepLink(this@MainActivity.intent)
                }
            }
        }
    }

    private fun handleAppleDeepLink(intent: Intent?) {
        val data: Uri = intent?.data ?: return
        if (data.scheme == AppleAuthConfig.APP_SCHEME &&
            data.host == AppleAuthConfig.APP_HOST) {
            Log.d("MainActivity", "Apple deep link raw: $data")
            Log.d("MainActivity", "Apple deep link query=${data.query}, fragment=${data.fragment}")
            var code: String? = data.getQueryParameter("code")
            var idToken: String? = data.getQueryParameter("id_token")
            val error = data.getQueryParameter("error")
            val fragment = data.fragment
            if (code == null && fragment?.contains("code=") == true) {
                val fragParams = Uri.parse("scheme://host?${fragment}")
                code = fragParams.getQueryParameter("code")
            }
            if (idToken == null && fragment?.contains("id_token=") == true) {
                val fragParams = Uri.parse("scheme://host?${fragment}")
                idToken = fragParams.getQueryParameter("id_token")
            }
            // Supabase implicit flow tokens (fragment)
            var accessToken: String? = null
            var refreshToken: String? = null
            var expiresIn: Long? = null
            var tokenType: String? = null
            if (fragment != null) {
                val fragParams = Uri.parse("scheme://host?${fragment}")
                accessToken = fragParams.getQueryParameter("access_token")
                refreshToken = fragParams.getQueryParameter("refresh_token")
                tokenType = fragParams.getQueryParameter("token_type")
                val expiresInStr = fragParams.getQueryParameter("expires_in")
                val expiresAtStr = fragParams.getQueryParameter("expires_at")
                expiresIn = when {
                    !expiresInStr.isNullOrBlank() -> expiresInStr.toLongOrNull()
                    !expiresAtStr.isNullOrBlank() -> {
                        val nowSec = System.currentTimeMillis() / 1000L
                        (expiresAtStr.toLongOrNull()?.minus(nowSec))?.coerceAtLeast(0L)
                    }
                    else -> null
                }
            }
            Log.d("MainActivity", "Apple deep link received. hasCode=${code != null}, hasIdToken=${idToken != null}, hasTokens=${accessToken != null && refreshToken != null}, error=$error")
            when {
                code != null -> {
                    if (::authViewModel.isInitialized) {
                        authViewModel.signInWithAppleCode(code, this)
                    } else {
                        Log.w("MainActivity", "authViewModel not initialized when deep link arrived (code)")
                    }
                }
                idToken != null -> {
                    if (::authViewModel.isInitialized) {
                        authViewModel.signInWithAppleIdToken(idToken, this)
                    } else {
                        Log.w("MainActivity", "authViewModel not initialized when deep link arrived (id_token)")
                    }
                }
                accessToken != null && refreshToken != null && expiresIn != null -> {
                    if (::authViewModel.isInitialized) {
                        val type = tokenType ?: "Bearer"
                        authViewModel.completeWithSupabaseTokens(accessToken, refreshToken, expiresIn!!, type, this)
                    } else {
                        Log.w("MainActivity", "authViewModel not initialized when deep link arrived (implicit tokens)")
                    }
                }
                error != null -> {
                    if (::authViewModel.isInitialized) {
                        authViewModel.setError("Apple sign-in error: $error")
                    }
                }
                else -> {
                    if (::authViewModel.isInitialized) {
                        authViewModel.setError("No authentication data received from Apple deep link")
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAppleDeepLink(intent)
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
}
package lc.fungee.IngrediCheck
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lc.fungee.IngrediCheck.ui.theme.IngrediCheckTheme
import lc.fungee.IngrediCheck.model.repository.DeviceRepository
import lc.fungee.IngrediCheck.model.repository.LoginAuthRepository
import lc.fungee.IngrediCheck.model.repository.PreferenceRepository
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
import lc.fungee.IngrediCheck.viewmodel.AppleLoginState
import lc.fungee.IngrediCheck.viewmodel.LoginAuthViewModelFactory
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import lc.fungee.IngrediCheck.ui.view.navigation.AppNavigation
import lc.fungee.IngrediCheck.ui.view.screens.home.ErrorScreen
//import lc.fungee.IngrediCheck.ui.screens.home.LoadingScreen
import lc.fungee.IngrediCheck.ui.view.screens.SplashScreen


import io.github.jan.supabase.auth.auth
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay


import lc.fungee.IngrediCheck.viewmodel.NetworkViewmodel
import lc.fungee.IngrediCheck.model.source.GoogleAuthDataSource
import lc.fungee.IngrediCheck.model.source.rememberGoogleSignInLauncher
import lc.fungee.IngrediCheck.model.entities.AppleAuthConfig
import lc.fungee.IngrediCheck.model.utils.AppConstants
import android.content.Intent


class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AppleAuthViewModel
    private lateinit var repository: LoginAuthRepository
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
        val preferenceRepository = PreferenceRepository(
            context = applicationContext,
            supabaseClient = repository.supabaseClient,
            functionsBaseUrl = AppConstants.Functions.base,
            anonKey = supabaseAnonKey
        )
        val deviceRepository = DeviceRepository(
            supabaseClient = repository.supabaseClient,
            functionsBaseUrl = AppConstants.Functions.base
        )
        val vmFactory = LoginAuthViewModelFactory(repository, deviceRepository)
        authViewModel = ViewModelProvider(this, vmFactory)
            .get(AppleAuthViewModel::class.java)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(activity = this@MainActivity)
            val isAuthChecked by authViewModel.isAuthChecked.collectAsState()
            var minSplashElapsed by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                // Enforce a minimum splash visibility to avoid instant skip on fast devices
                delay(900)
                minSplashElapsed = true
            }
            IngrediCheckTheme {
                if (!isAuthChecked || !minSplashElapsed) {
                    // Show custom splash while auth initializes; navigation proceeds when isAuthChecked becomes true
                    SplashScreen(onSplashFinished = { /* no-op */ })
                } else {
                    AppNavigation(
                        viewModel = authViewModel,
                        supabaseClient = repository.supabaseClient,
                        windowSize = windowSizeClass,
                        functionsBaseUrl = AppConstants.Functions.base,
                        anonKey = AppConstants.Supabase.ANON_KEY
                    )
                }
            }
        }
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
}
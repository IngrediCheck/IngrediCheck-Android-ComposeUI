package lc.fungee.IngrediCheck.ui.view.navigation

// Updated: app/src/main/java/lc/fungee/IngrediCheck/ui/view/navigation/AppNavigation.kt
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import lc.fungee.IngrediCheck.model.entities.AppleAuthConfig
import lc.fungee.IngrediCheck.model.repository.PreferenceRepository
import lc.fungee.IngrediCheck.model.source.GoogleAuthDataSource
import lc.fungee.IngrediCheck.model.source.rememberGoogleSignInLauncher
import lc.fungee.IngrediCheck.ui.view.screens.home.HomeScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.ListScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.FavoritesPageScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.RecentScansPageScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.HistoryItemDetailScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.FavoriteItemDetailScreen
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
import lc.fungee.IngrediCheck.viewmodel.NetworkViewmodel
import lc.fungee.IngrediCheck.viewmodel.PreferenceViewModel
import lc.fungee.IngrediCheck.ui.view.component.NetworkStatusOverlay
import lc.fungee.IngrediCheck.ui.view.screens.SplashScreen
import lc.fungee.IngrediCheck.ui.view.screens.onboarding.DisclaimerScreen
import lc.fungee.IngrediCheck.ui.view.screens.onboarding.WelcomeScreen
import lc.fungee.IngrediCheck.ui.view.screens.setting.SettingScreen
import java.net.URLDecoder
import lc.fungee.IngrediCheck.model.utils.AppConstants

@Composable
fun AppNavigation(
    viewModel: AppleAuthViewModel,
    supabaseClient: SupabaseClient,
    windowSize: WindowSizeClass,
    functionsBaseUrl: String,
    anonKey: String
) {
    val navController = rememberNavController()
    val ctx = LocalContext.current

    // Network status
    val networkViewModel: NetworkViewmodel = viewModel()
    LaunchedEffect(Unit) {
        networkViewModel.startMonitoring(ctx.applicationContext)
    }
    // Draw overlay above navigation content; Box ensures correct z-order

    // Google Sign-In
    val googleSignInClient: GoogleSignInClient = remember { GoogleAuthDataSource.getClient(ctx) }
    val activity = ctx as? Activity
    val googleSignInLauncher = rememberGoogleSignInLauncher(activity, viewModel)

    // Keep custom splash as start destination; compute where to go after splash delay
    // Logged-in status must reflect actual Supabase session presence only.
    val isLoggedIn: Boolean = remember {
        runCatching { supabaseClient.auth.currentSessionOrNull() != null }.getOrDefault(false)
    }
    val disclaimerAccepted: Boolean = remember {
        ctx.getSharedPreferences(
            AppConstants.Prefs.USER_SESSION,
            android.content.Context.MODE_PRIVATE
        )
            .getBoolean(AppConstants.Prefs.KEY_DISCLAIMER_ACCEPTED, false)
    }

    // On app launch, if there is no active session, clear any stale login_provider flag
    // so the UI doesn't think the user is logged in as a guest.
    LaunchedEffect(Unit) {
        val hasSession = runCatching { supabaseClient.auth.currentSessionOrNull() != null }.getOrDefault(false)
        if (!hasSession) {
            ctx.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                .edit()
                .remove(AppConstants.Prefs.KEY_LOGIN_PROVIDER)
                .apply()
        }
    }

    // Preference VM
    val preferenceViewModel = remember {
        PreferenceViewModel(
            PreferenceRepository(
                context = ctx,
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        )
    }

    // Handle initial deep link if app launched via Apple redirect
    LaunchedEffect(Unit) {
        val data = (ctx as? Activity)?.intent?.data
        if (data != null && data.scheme == AppleAuthConfig.APP_SCHEME && data.host == AppleAuthConfig.APP_HOST) {
            Log.d("AppNavigation", "Apple deep link raw: $data")
            val fragment = data.fragment
            var code: String? = data.getQueryParameter("code")
            var idToken: String? = data.getQueryParameter("id_token")
            if (code == null && fragment?.contains("code=") == true) {
                val fragParams = Uri.parse("scheme://host?${fragment}")
                code = fragParams.getQueryParameter("code")
            }

            if (idToken == null && fragment?.contains("id_token=") == true) {
                val fragParams = Uri.parse("scheme://host?${fragment}")
                idToken = fragParams.getQueryParameter("id_token")
            }
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
            when {
                code != null -> viewModel.signInWithAppleCode(code, ctx)
                idToken != null -> viewModel.signInWithAppleIdToken(idToken, ctx)
                accessToken != null && refreshToken != null && expiresIn != null -> {
                    viewModel.completeWithSupabaseTokens(
                        accessToken,
                        refreshToken,
                        expiresIn!!,
                        tokenType ?: "Bearer",
                        ctx
                    )
                }

                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Compute the true start destination reactively based on auth state
        val loginState by viewModel.loginState.collectAsState()
        val isAuthChecked by viewModel.isAuthChecked.collectAsState()
        
        if (!isAuthChecked) {
            // Show loading until auth state is determined
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val startDestination = remember(loginState, isAuthChecked) {
                val prefs = ctx.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                val accepted = prefs.getBoolean(AppConstants.Prefs.KEY_DISCLAIMER_ACCEPTED, false)
                val hasSdkSession = runCatching { supabaseClient.auth.currentSessionOrNull() != null }.getOrDefault(false)
                
                Log.d(
                    "AppNavigation", 
                    "Computing startDestination: hasSdkSession=$hasSdkSession, accepted=$accepted, loginState=$loginState, isAuthChecked=$isAuthChecked"
                )
                
                val destination = when {
                    hasSdkSession && accepted -> "home"
                    hasSdkSession && !accepted -> "disclaimer"
                    else -> "welcome"
                }
                
                Log.d("AppNavigation", "startDestination computed as: $destination")
                destination
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            ) {

            composable("welcome") {
                WelcomeScreen(
                    onGoogleSignIn = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    viewModel = viewModel,
                    navController = navController,
                    googleSignInClient = googleSignInClient
                )
            }

            composable("disclaimer") {
                val hasSession = runCatching { supabaseClient.auth.currentSessionOrNull() != null }.getOrDefault(false)
                if (!hasSession) {
                    LaunchedEffect(Unit) {
                        navController.navigate("welcome") {
                            popUpTo("disclaimer") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    DisclaimerScreen(
                        modifier = Modifier.fillMaxSize(),
                        onAgree = {
                            // Persist that the user has accepted the disclaimer so it only shows once
                            ctx.getSharedPreferences(
                                AppConstants.Prefs.USER_SESSION,
                                Context.MODE_PRIVATE
                            )
                                .edit()
                                .putBoolean(AppConstants.Prefs.KEY_DISCLAIMER_ACCEPTED, true)
                                .apply()
                            navController.navigate("home") {
                                popUpTo("disclaimer") { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable("home") {
                HomeScreen(
                    preferenceViewModel = preferenceViewModel,
                    supabaseClient = supabaseClient,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey,
                    viewModel = viewModel,
                    googleSignInClient = googleSignInClient,
                    navController = navController
                )
            }

            composable("List") {
                ListScreen(
                    navController = navController,
                    viewModel = viewModel,
                    supabaseClient = supabaseClient,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey
                )
            }
            composable("favoritesAll") {
                FavoritesPageScreen(
                    supabaseClient = supabaseClient,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey,
                    navController = navController
                )
            }
            composable("recentScansAll") {
                RecentScansPageScreen(
                    supabaseClient = supabaseClient,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey,
                    navController = navController
                )
            }
            composable("historyItem?item={item}") { backStackEntry ->
                val raw = backStackEntry.arguments?.getString("item") ?: ""
                val itemJson = try {
                    URLDecoder.decode(raw, "UTF-8")
                } catch (_: Exception) {
                    raw
                }
                HistoryItemDetailScreen(
                    itemJson = itemJson,
                    supabaseClient = supabaseClient,
                    navController = navController,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey
                )
            }
            composable("favoriteItem?item={item}") { backStackEntry ->
                val raw = backStackEntry.arguments?.getString("item") ?: ""
                val itemJson = try {
                    URLDecoder.decode(raw, "UTF-8")
                } catch (_: Exception) {
                    raw
                }
                FavoriteItemDetailScreen(
                    itemJson = itemJson,
                    supabaseClient = supabaseClient,
                    navController = navController,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey
                )
            }
            composable("setting") {
                SettingScreen(
                    preferenceViewModel = preferenceViewModel,
                    onDismiss = { navController.popBackStack() },
                    supabaseClient = supabaseClient,
                    onRequireReauth = {
                        navController.navigate("welcome") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    viewModel = viewModel,
                    googleSignInClient = googleSignInClient
                )
            }
        }
        }
        // Draw the offline overlay above all destinations
        NetworkStatusOverlay(isOnline = networkViewModel.isOnline)
    }
}

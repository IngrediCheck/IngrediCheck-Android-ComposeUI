package lc.fungee.IngrediCheck.ui.view.navigation

// Updated: app/src/main/java/lc/fungee/IngrediCheck/ui/view/navigation/AppNavigation.kt
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import lc.fungee.IngrediCheck.ui.view.screens.home.HomeScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.ListScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.FavoritesPageScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.RecentScansPageScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.HistoryItemDetailScreen
import lc.fungee.IngrediCheck.ui.view.screens.list.FavoriteItemDetailScreen
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
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
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    googleSignInClient: GoogleSignInClient,
    preferenceViewModel: PreferenceViewModel?,
    supabaseClient: SupabaseClient,
    windowSize: WindowSizeClass,
    isOnline: Boolean,
    functionsBaseUrl: String,
    anonKey: String
) {
    val navController = rememberNavController()
    NetworkStatusOverlay(isOnline = isOnline)

    // Keep custom splash as start destination; compute where to go after splash delay
    val ctx = LocalContext.current
    val isLoggedIn: Boolean = remember {
        val prefs = ctx.getSharedPreferences(AppConstants.Prefs.USER_SESSION, android.content.Context.MODE_PRIVATE)
        val provider = prefs.getString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, null)
        val hasSdkSession = runCatching { supabaseClient.auth.currentSessionOrNull() != null }.getOrDefault(false)
        hasSdkSession || (provider == AppConstants.Providers.ANONYMOUS)
    }
    val disclaimerAccepted: Boolean = remember {
        ctx.getSharedPreferences(AppConstants.Prefs.USER_SESSION, android.content.Context.MODE_PRIVATE)
            .getBoolean(AppConstants.Prefs.KEY_DISCLAIMER_ACCEPTED, false)
    }
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    val destination = when {
                        isLoggedIn && disclaimerAccepted -> "home"
                        isLoggedIn && !disclaimerAccepted -> "disclaimer"
                        else -> "welcome"
                    }
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

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
            DisclaimerScreen(
                modifier = Modifier.fillMaxSize(),
                onAgree = {
                    // Persist that the user has accepted the disclaimer so it only shows once
                    ctx.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(AppConstants.Prefs.KEY_DISCLAIMER_ACCEPTED, true)
                        .apply()
                    navController.navigate("home") {
                        popUpTo("disclaimer") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            if (preferenceViewModel != null) {
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
            val itemJson = try { URLDecoder.decode(raw, "UTF-8") } catch (_: Exception) { raw }
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
            val itemJson = try { URLDecoder.decode(raw, "UTF-8") } catch (_: Exception) { raw }
            FavoriteItemDetailScreen(
                itemJson = itemJson,
                supabaseClient = supabaseClient,
                navController = navController,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
        composable ("setting"){
            if (preferenceViewModel != null) {
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
}

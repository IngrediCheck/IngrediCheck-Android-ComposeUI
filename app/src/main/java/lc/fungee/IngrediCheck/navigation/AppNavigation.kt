// Updated: app/src/main/java/lc/fungee/IngrediCheck/AppNavigation.kt
package lc.fungee.IngrediCheck.navigation
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.github.jan.supabase.SupabaseClient
//import lc.fungee.IngrediCheck.ui.screens.check.CameraScreen
import lc.fungee.IngrediCheck.ui.screens.home.HomeScreen
import lc.fungee.IngrediCheck.ui.screens.list.ListScreen
import lc.fungee.IngrediCheck.ui.screens.list.FavoritesPageScreen
import lc.fungee.IngrediCheck.ui.screens.list.RecentScansPageScreen
import lc.fungee.IngrediCheck.ui.screens.list.HistoryItemDetailScreen
import lc.fungee.IngrediCheck.ui.screens.list.FavoriteItemDetailScreen
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.data.repository.PreferenceViewModel
import lc.fungee.IngrediCheck.ui.component.NetworkStatusOverlay
import lc.fungee.IngrediCheck.ui.screens.SplashScreen
//import lc.fungee.IngrediCheck.ui.screens.check.AnalysisScreen
//import lc.fungee.IngrediCheck.ui.screens.home.LoadingScreen
import lc.fungee.IngrediCheck.ui.screens.onboarding.DisclaimerScreen
import lc.fungee.IngrediCheck.ui.screens.onboarding.WelcomeScreen
import lc.fungee.IngrediCheck.ui.screens.setting.SettingScreen
import java.net.URLDecoder

@Composable
fun AppNavigation(
    viewModel: AppleAuthViewModel,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    googleSignInClient: GoogleSignInClient,
    preferenceViewModel: PreferenceViewModel?,
    supabaseClient: SupabaseClient,
    windowSize: WindowSizeClass,  // ✅ Fixed: proper type and name
    isOnline: Boolean,
    functionsBaseUrl: String,
    anonKey: String
) {
    val navController = rememberNavController()
    NetworkStatusOverlay(isOnline = isOnline)
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
               // windowSize = windowSize,  // ✅ Fixed: proper parameter name
                onSplashFinished = { isLoggedIn ->
                    navController.navigate(if (isLoggedIn) "home" else "welcome") {
                        popUpTo("splash") { inclusive = true }
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
                    navController.navigate("home") {
                        popUpTo("disclaimer") { inclusive = true }
                    }
                }
            )
        }

        // ✅ Updated: Pass navController to all screens
        composable("home") {
            if (preferenceViewModel != null) {
                HomeScreen(
                    navController = navController,
                    preferenceViewModel = preferenceViewModel,
                    supabaseClient = supabaseClient,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey,
                    viewModel = viewModel,
                    googleSignInClient = googleSignInClient
                )
            } else {
                // Show a loading or error screen, or redirect to login
//        LoadingScreen()

            }
        }

//        composable("Check") {
//            CheckBottomSheet(
//                navController = navController,
//                onDismiss = {
//                    // what should happen when sheet closes?
//                    navController.popBackStack() // example: go back
//                }
//            )
//        }


//        composable("checkTab/{barcode}") { backStackEntry ->
//            val barcode = backStackEntry.arguments?.getString("barcode")
//            CheckTabScreen(barcode = barcode)
//        }

//        composable("analysis/{barcode}") { backStackEntry ->
//            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
//            val supabaseUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co"
//            val functionsBaseUrl = "$supabaseUrl/functions/v1/ingredicheck"
//            val anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI"
//
//            AnalysisScreen(
//                barcode = barcode,
//                supabaseClient = supabaseClient,
//                functionsBaseUrl = functionsBaseUrl,
//                anonKey = anonKey
//            )
//        }

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
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
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

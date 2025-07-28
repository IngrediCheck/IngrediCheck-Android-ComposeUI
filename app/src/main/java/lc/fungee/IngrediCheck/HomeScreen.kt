package lc.fungee.IngrediCheck

import android.content.Context

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import lc.fungee.IngrediCheck.auth.SupabaseSession
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: AppleAuthViewModel) {
    val context = LocalContext.current

    // Retrieve session from SharedPreferences
    val sessionJson = remember {
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            .getString("session", null)
    }

    val session = remember {
        sessionJson?.let {
            Gson().fromJson(it, SupabaseSession::class.java)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Welcome to Home!", style = MaterialTheme.typography.headlineMedium)

        session?.user?.let { user ->
            Text("User ID: ${user.id}")
            Text("Email: ${user.email}")
        } ?: Text("No user data found.")

        Button(
            onClick = {
                // Clear session
                context.getSharedPreferences("user_session", Context.MODE_PRIVATE).edit().clear().apply()
                // Reset login state
                viewModel.resetState()
                // Navigate to welcome screen
                navController.navigate("welcome") {
                    popUpTo("home") { inclusive = true }
                }
            }
        ) {
            Text("Logout")
        }
    }
}

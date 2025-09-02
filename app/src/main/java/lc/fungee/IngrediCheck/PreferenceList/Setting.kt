// Updated: app/src/main/java/lc/fungee/IngrediCheck/PreferenceList/Setting.kt

package lc.fungee.IngrediCheck.PreferenceList

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.auth.SupabaseSession
import lc.fungee.IngrediCheck.ui.theme.White

@Composable
fun SettingsScreen(navController: NavController,viewModel: AppleAuthViewModel) {
    val context = LocalContext.current
    val sessionJson = remember {
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            .getString("session", null)
    }

    val session = remember {
        sessionJson?.let {
            Gson().fromJson(it, SupabaseSession::class.java)
        }
    }
    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues).background(color=White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // User Information Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "User Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    session?.user?.let { user ->
                        Text("User ID: ${user.id}")
                        Text("Email: ${user.email}")
                    } ?: Text("No user data found.")
                    // Logout Button in the middle
                    Button(
                        onClick = {
                            // Clear session
                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE).edit().clear().apply()
                            // Reset login state
                            viewModel.resetState()
                            // Navigate to welcome screen
                            navController.navigate("welcome") {
                                popUpTo("settings") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                }
            }
        }
    }

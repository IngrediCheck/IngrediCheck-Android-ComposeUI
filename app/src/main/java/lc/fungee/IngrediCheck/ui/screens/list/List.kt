// Updated: app/src/main/java/lc/fungee/IngrediCheck/PreferenceList/List.kt

package lc.fungee.IngrediCheck.ui.screens.list

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.data.model.SupabaseSession
import lc.fungee.IngrediCheck.ui.component.BottomBar
import lc.fungee.IngrediCheck.ui.screens.check.CheckBottomSheet
import lc.fungee.IngrediCheck.ui.theme.White
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100

@Composable
fun ListScreen(
    navController: NavController,
    viewModel: AppleAuthViewModel,
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String
) {
    var showSheet by remember { mutableStateOf(false) }
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
        bottomBar = { BottomBar(navController = navController, onCheckClick = { showSheet = true })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.background(White)
                .fillMaxSize()
                .padding(horizontal = 16.dp).padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header
            Text(
                text = "Lists",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.SansSerif, // Replace with SF Pro if added
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    letterSpacing = (-0.41).sp,
                    color = Color(0xFF1B270C),
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,

            ){
                Text(
                    text = "My List",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        letterSpacing = (-0.41).sp,
                        color = Color(0xFF1B270C),
                        lineHeight = 22.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                // Right text
                Icon(
                    imageVector = Icons.Default.Add,
                    "",
                    tint = PrimaryGreen100,

                )

                Text(
                    text = " New List", // you can make this dynamic later

                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = PrimaryGreen100,
                        lineHeight = 22.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 3. Empty Activity Image
            LazyRow (){
                item {
                    Image(
                        painter = painterResource(id = R.drawable.foodemptylist), // put your drawable
                        contentDescription = "Empty List",
                        modifier = Modifier
                            .size(width = 160.dp, height = 170.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            // 4. Recent Scan
            Text(
                text = "Recent Scan",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    letterSpacing = (-0.41).sp,
                    color = Color(0xFF1B270C),
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Empty Recent Scan Image
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.emptyrecentscan),
                    contentDescription = "Empty Recent Scan",
                    modifier = Modifier.size(width = 174.dp, height = 134.dp)
                )
            }

        }
        }


//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues).background(color=White)
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            // User Information Section
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 32.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "User Information",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
//                    session?.user?.let { user ->
//                        Text("User ID: ${user.id}")
//                        Text("Email: ${user.email}")
//                    } ?: Text("No user data found.")
//                    // Logout Button in the middle
//                    Button(
//                        onClick = {
//                            // Clear session
//                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE).edit().clear().apply()
//                            // Reset login state
//                            viewModel.resetState()
//                            // Navigate to welcome screen
//                            navController.navigate("welcome") {
//                                popUpTo("settings") { inclusive = true }
//                            }
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(56.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.error
//                        )
//                    ) {
//                        Text(
//                            text = "Logout",
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
//                }
//                }
//            }

        if (showSheet) {
            CheckBottomSheet(
                onDismiss = { showSheet = false },
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
        }
//    }

package lc.fungee.IngrediCheck.auth

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import android.widget.Toast
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R

@Composable
fun AppleSignInSection(
    viewModel: AppleAuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val loginState by viewModel.loginState.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Button(
            onClick = {
                if (activity != null) {
                    // Use the WebView-based Apple login
                    val repository = viewModel.javaClass.getDeclaredField("repository").apply { isAccessible = true }.get(viewModel) as AppleAuthRepository
                    repository.launchAppleLoginWebView(
                        activity = activity,
                        clientId = "llc.fungee.ingredicheck.web", // Your Apple Service ID
                        redirectUri = "io.supabase.ingredicheck://login-callback", // Your redirect URI
                        onSuccess = { token: String ->
                            Toast.makeText(activity, "Apple WebView Success: $token", Toast.LENGTH_LONG).show()
                        },
                        onError = { error: String ->
                            Toast.makeText(activity, "Apple WebView Error: $error", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF789D0E)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.applelogo),
                    contentDescription = "Google logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Apple (WebView)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }




//        Spacer(modifier = Modifier.height(8.dp))
//
//        Button(
//            onClick = {
//                if (activity != null) {
//                    // Use the device browser for Apple login
//                    val repository = viewModel.javaClass.getDeclaredField("repository").apply { isAccessible = true }.get(viewModel) as AppleAuthRepository
//                    repository.launchAppleLoginInBrowser(
//                        activity = activity,
//                        clientId = "llc.fungee.ingredicheck.web", // Your Apple Service ID
//                        redirectUri = "https://wqidjkpfdrvomfkmefqc.supabase.co/auth/v1/callback" // Your redirect URI
//                    )
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//        ) {
//            Text("Sign in with Apple (Browser)")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))

        when (loginState) {
            is AppleLoginState.Loading -> {
                CircularProgressIndicator()
            }
            is AppleLoginState.Error -> {
                Text(
                    text = (loginState as AppleLoginState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is AppleLoginState.Success -> {
                Text(
                    text = "Login successful!",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            else -> {}
        }
    }
} 
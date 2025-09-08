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
                    // Launch WebView-based Apple login via ViewModel
                    viewModel.launchAppleWebViewLogin(activity)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Sign in with Apple (AppAuth)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (activity != null) {
                    // Directly use repository to launch WebView-based Apple login
                    // The repository method does not take clientId/redirectUri here; it's configured internally
                    val repository = viewModel.javaClass.getDeclaredField("repository").apply { isAccessible = true }.get(viewModel) as AppleAuthRepository
                    repository.launchAppleLoginWebView(activity)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Sign in with Apple (WebView)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(16.dp))

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
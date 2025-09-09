package lc.fungee.IngrediCheck.auth

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState

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
                activity?.let { viewModel.launchAppleWebViewLogin(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Sign in with Apple")
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
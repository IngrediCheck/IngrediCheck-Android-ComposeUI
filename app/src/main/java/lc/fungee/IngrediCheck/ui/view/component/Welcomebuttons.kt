package lc.fungee.IngrediCheck.ui.view.component

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
import lc.fungee.IngrediCheck.model.source.AppleSignInManager
import lc.fungee.IngrediCheck.viewmodel.AppleLoginState
import lc.fungee.IngrediCheck.ui.theme.AppColors


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
                        Log.d("AppleSignIn", "Apple login (native) clicked")
                        AppleSignInManager.startAppleSignIn(activity)
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Brand),
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
                        painter = painterResource(id = R.drawable.applelogowhite),
                        contentDescription = "Apple logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign in with Apple",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Show spinner only for Apple/Google flows (controlled by isAppleLoading).
            if (viewModel.isAppleLoading) {
                Text("Loading...", color = Color.Gray)
                CircularProgressIndicator()
            } else when (loginState) {
                is AppleLoginState.Error -> {
                    Text(
                        text = (loginState as AppleLoginState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is AppleLoginState.Success -> {
                    // Login successful - handled by navigation
                }
                else -> {
                    // Ready to login
                }
            }
        }
    }


    @Composable
    fun GoogleSignInButton(
        context: Context,
        onGoogleSignIn: (() -> Unit)?
    ) {
        Button(
            onClick = { onGoogleSignIn?.invoke() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Brand),
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
                    painter = painterResource(id = R.drawable.social_icon),
                    contentDescription = "Google logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Google",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }


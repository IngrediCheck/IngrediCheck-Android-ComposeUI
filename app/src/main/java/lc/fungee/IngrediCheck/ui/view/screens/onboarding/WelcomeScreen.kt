package lc.fungee.IngrediCheck.ui.view.screens.onboarding
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
import lc.fungee.IngrediCheck.viewmodel.AppleLoginState
import lc.fungee.IngrediCheck.ui.view.component.AppleSignInSection
import lc.fungee.IngrediCheck.ui.view.component.GoogleSignInButton
import lc.fungee.IngrediCheck.ui.theme.Greyscale200
import lc.fungee.IngrediCheck.ui.theme.AppColors



val fredokaMedium = FontFamily(Font(R.font.fredoka_medium))
val termsTag = "TERMS"
val privacyTag = "PRIVACY"
@Composable
fun WelcomeScreen(
    onGoogleSignIn: (() -> Unit)? = null,
    viewModel: AppleAuthViewModel,
    navController: NavController,
    googleSignInClient: GoogleSignInClient
) {
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Navigation logic moved to LaunchedEffect
    LaunchedEffect(loginState) {
        Log.d("WelcomeScreen", "Login state changed: $loginState")
        when (loginState) {
            is AppleLoginState.Success -> {
                Log.d("WelcomeScreen", "Login successful, navigating to disclaimer")
                navController.navigate("disclaimer") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            is AppleLoginState.NavigateToDisclaimer -> {
                Log.d("WelcomeScreen", "Navigating to disclaimer from MainActivity")
                navController.navigate("disclaimer") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            is AppleLoginState.Error -> {
                Log.e("WelcomeScreen", "Login error: $loginState")
                // You can show a snackbar or error message here
            }
            else -> {
                // Handle other states if needed
            }
        }
    }

    // Removed sessionStoreFailed handling as it's not exposed by the ViewModel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 👇 Onboarding Pager with flexible height
        Box(
//            modifier = Modifier
//                .weight(1f) // Take available space
//                .fillMaxWidth()
            modifier = Modifier
                .padding(top = 84.dp, start = 16.dp) // position the Box from parent
                .size(width = 343.dp, height = 488.dp) // exact size
                .graphicsLayer(rotationZ = 0f, alpha = 1f) // angle + opacity
        ) {
            val welcomeItems = WelcomeScreenItemsManager.getOnboardingItems()
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { welcomeItems.size }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                WelcomePager(
                    item = welcomeItems[page],
                    currentPage = pagerState.currentPage,
                    totalPages = welcomeItems.size
                )
            }
        }

        // Reduced spacer for better spacing
        Spacer(modifier = Modifier.height(32.dp))

        // Google Sign-In Button
        GoogleSignInButton(
            context = context,
            onGoogleSignIn = onGoogleSignIn
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Apple Sign-In Section (consolidated)
        AppleSignInSection(viewModel = viewModel)

        // Continue as Guest with better spacing
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Continue as guest",
            color = AppColors.Brand,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
                Log.d("WelcomeScreen", "Anonymous sign-in clicked")
                viewModel.signInAnonymously(context)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Terms and Privacy
        val annotatedText = buildAnnotatedString {
            append("By continuing, you are agreeing to our\n")

            pushStyle(
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontFamily = FontFamily(Font(R.font.inter_28pt_regular)),
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Normal,
                    fontSize = 13.sp,
                    letterSpacing = (-0.08).sp,
                )
            )
            pushStringAnnotation(tag = termsTag, annotation = "https://www.ingredicheck.app/terms-conditions")
            append("Terms of use")
            pop()
            pop()

            append(" and ")

            pushStyle(
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontFamily = FontFamily(Font(R.font.inter_24pt_regular)),
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Normal,
                    fontSize = 13.sp,
                    letterSpacing = (-0.08).sp
                )
            )
            pushStringAnnotation(tag = privacyTag, annotation = "https://www.ingredicheck.app/privacy-policy")
            append("Privacy Policy")
            pop()
            pop()
        }

        val uriHandler = LocalUriHandler.current

        ClickableText(
            text = annotatedText,
            style = TextStyle(
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            ),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = termsTag, start = offset, end = offset)
                    .firstOrNull()?.let { sa ->
                        uriHandler.openUri(sa.item)
                        return@ClickableText
                    }
                annotatedText.getStringAnnotations(tag = privacyTag, start = offset, end = offset)
                    .firstOrNull()?.let { sa ->
                        uriHandler.openUri(sa.item)
                    }
            }
        )
    }
}

// ✅ Consolidated WelcomePager from WelcomePage.kt
@Composable
fun WelcomePager(
    item: WelcomeOnboardingItem,
    modifier: Modifier = Modifier,
    currentPage: Int = 0,
    totalPages: Int = 1
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ✅ Image with flexible height
        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Reduced height for better proportion
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ Heading with Fredoka Medium font
        Text(
            text = item.heading,
            style = TextStyle(
                fontFamily = fredokaMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                letterSpacing = 0.36.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Description with better visibility
        Text(
            text = item.description,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                letterSpacing = (-0.41).sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ Pager Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (currentPage == index) AppColors.Brand else Greyscale200,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}




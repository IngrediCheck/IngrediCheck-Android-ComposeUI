package lc.fungee.IngrediCheck.ui.view.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import lc.fungee.IngrediCheck.R

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier.Companion,
    onSplashFinished: (Boolean) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(2000)
        val session = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            .getString("session", null)
        onSplashFinished(session != null)
    }

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(Color.Companion.White)
    ) {
        // Left image
        Image(
            painter = painterResource(id = R.drawable.leftlogo),
            contentDescription = "Left Logo",
            contentScale = ContentScale.Companion.Fit,
            modifier = Modifier.Companion
                .size(376.dp)
                .align(Alignment.Companion.TopStart)
                .offset(x = (-90).dp, y = (-1).dp)
        )

        // Center logo
        Image(
            painter = painterResource(id = R.drawable.ingredichecklogo),
            contentDescription = "Main Logo",
            contentScale = ContentScale.Companion.Fit,
            modifier = Modifier.Companion
                .size(206.dp)
                .align(Alignment.Companion.Center)
        )

        // Right image
        Image(
            painter = painterResource(id = R.drawable.rightlogo),
            contentDescription = "Right Logo",
            contentScale = ContentScale.Companion.Fit,
            modifier = Modifier.Companion
                .size(376.dp)
                .align(Alignment.Companion.BottomEnd)
                .offset(x = 100.dp, y = (-1).dp)
        )
    }
}
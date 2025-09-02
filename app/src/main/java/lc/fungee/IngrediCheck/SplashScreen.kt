package lc.fungee.IngrediCheck
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Left image
        Image(
            painter = painterResource(id = R.drawable.leftlogo),
            contentDescription = "Left Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(376.dp)
                .align(Alignment.TopStart)
                .offset(x = (-90.79).dp, y = (-10).dp)
        )

        // Center logo
        Image(
            painter = painterResource(id = R.drawable.ingredichecklogo),
            contentDescription = "Main Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(206.dp)
                .align(Alignment.Center)
        )

        // Right image
        Image(
            painter = painterResource(id = R.drawable.rightlogo),
            contentDescription = "Right Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(376.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = (-10).dp)
        )
    }
}


package lc.fungee.IngrediCheck
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
    onSplashFinished: (Boolean) -> Unit // Pass login state
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(2000) // Optional splash delay


        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val sessionString = sharedPref.getString("session", null)

        // Navigate based on session
        onSplashFinished(sessionString != null)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.leftlogo),
            contentDescription = "Left Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(376.dp)
                .height(427.dp)
                .offset(x = (-90.79).dp, y = (-10).dp)

        )






        Image(
            painter = painterResource(id = R.drawable.ingredichecklogo),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(206.27.dp)
                .height(184.76.dp)
                .align(alignment = Alignment.Center)

        )
        Image(
            painter = painterResource(id = R.drawable.rightlogo),
            contentDescription = "Left Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(376.dp)
                .height(427.dp).align(Alignment.BottomEnd)
                .offset(x = (100).dp, y = (-10).dp)

        )


    }

}
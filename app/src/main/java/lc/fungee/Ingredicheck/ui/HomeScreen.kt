package lc.fungee.Ingredicheck.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.components.MyIcon
import lc.fungee.Ingredicheck.ui.components.TabBar
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale130
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.titleTextStyle


@Composable
fun HomeScreen(
    displayName: String = "Bite Buddy",
    avatarImageUrl: String? = null,
    onRecentScansTap: () -> Unit = {},
    onChatBotTap: () -> Unit = {},
    onScannerTap: () -> Unit = {}
) {
    // Extra bottom padding so the tab bar sits above the 3-button system navigation bar.
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val tabBarBottomPadding = navBarBottomPadding + 16.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 44.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
//                    vertical = 24.dp
                )
                .align(Alignment.TopStart)
        ) {
            // Greeting header at the top, similar to iOS HomeView.
            Row(
                modifier = Modifier
                    .fillMaxWidth()

                    .padding(bottom = 28.dp)
//                    .align(Alignment.TopStart),
//                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Hello \uD83D\uDC4B",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.dp.value.sp,
                        color = Greyscale150
                    )

                    Text(
                        text = "$displayName!",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.dp.value.sp,
                        color = Greyscale150,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Your food notes, personalized for you.",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.dp.value.sp,
                        color = Greyscale130,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

//                Spacer(modifier = Modifier.size(12.dp))

                ProfileAvatarCircle(
                    avatarImageUrl = avatarImageUrl,
                    displayName = displayName
                )

            }
            // ✅ NEW ROW YOU WANT TO ADD
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

Column(modifier = Modifier.weight(1f).background(Color.Red)) {

    Text(
        text = "Food Notes",
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = Greyscale150,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "Here’s what your family\navoids  or needs to watch\nout for.",
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Greyscale100,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}
                Box(
                    modifier = Modifier.weight(1f).background(Color.Yellow)
                      ,
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberVectorPainter(image = MyIcon),
                        contentDescription = "MyIcon preview",
                        modifier = Modifier.size(193.dp, 214.dp)
                    )
                }



            }
        }




        TabBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = tabBarBottomPadding),
            isExpanded = true,
            onRecentScansTap = onRecentScansTap,
            onChatBotTap = onChatBotTap,
            onScannerTap = onScannerTap
        )
    }
}

@Composable
private fun ProfileAvatarCircle(
    avatarImageUrl: String?,
    displayName: String
) {
    Box(
        modifier = Modifier
            .size(66.dp)
            .clip(CircleShape)
            .border(width = 4.dp, color = Color(0xFFECECEC), shape = CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        when {
            !avatarImageUrl.isNullOrBlank() -> {
                SubcomposeAsyncImage(
                    model = avatarImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is coil.compose.AsyncImagePainter.State.Loading -> {
                            // Simple subtle placeholder while loading
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Greyscale120),
                                contentAlignment = Alignment.Center
                            ) { }
                        }

                        else -> SubcomposeAsyncImageContent()
                    }
                }
            }

            else -> {
                // Fallback: colored circle with the first initial of the name.
                val initial = displayName.trim().firstOrNull()?.uppercase() ?: "?"
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Greyscale120),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.dp.value.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
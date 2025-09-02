package lc.fungee.IngrediCheck.PreferenceList

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Greyscale200
import lc.fungee.IngrediCheck.ui.theme.Greyscale50
import lc.fungee.IngrediCheck.ui.theme.Greyscale500
import lc.fungee.IngrediCheck.ui.theme.White
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import lc.fungee.IngrediCheck.ui.theme.PrimarayGreen50
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

import lc.fungee.IngrediCheck.ui.theme.Greyscale700
import androidx.lifecycle.viewmodel.compose.viewModel
import lc.fungee.IngrediCheck.ui.screen.home.HomeViewModel
import lc.fungee.IngrediCheck.ui.screen.home.HomeViewModelFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            context = context,
            supabaseUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co",
            supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI"
        )
    )
    val state = viewModel.state.collectAsState().value
    var text by remember { mutableStateOf(state.input) }
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var submittedText by remember { mutableStateOf("") }

    // On first launch, fetch preferences
    LaunchedEffect(true) { viewModel.load() }

    // Track focus state
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused = true
                is FocusInteraction.Unfocus -> isFocused = false
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your dietary preference",

                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 10.dp)
                    .width(189.dp)
                    .height(22.dp),
                style = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    letterSpacing = (-0.41).sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .then(
                        if (isFocused) {
                            Modifier
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(8.dp),
                                    ambientColor = Color(0xFFCBEB6E),
                                    spotColor = PrimaryGreen100
                                )
                        } else Modifier
                    )
            ) {
                OutlinedTextField(
                    value = state.input,
                    onValueChange = { viewModel.onInputChange(it) },
                    placeholder = {
                        Text(
                            text = "Enter dietary preference here",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 21.sp,
                                letterSpacing = (-0.32).sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(0.5f)
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal, // 400
                        fontStyle = FontStyle.Normal,
                        lineHeight = 21.sp,
                        letterSpacing = (-0.32).sp,
//                        fontFamily =, // <-- Use your actual font
                        color = Greyscale700// Your desired text color
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
//                        .height(53.dp),
                    , keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.save() }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = PrimaryGreen100,
                        unfocusedBorderColor = Color.Transparent,
                        backgroundColor = Greyscale50,
                        cursorColor = PrimaryGreen100
                    ),
                    maxLines = 5,
                    interactionSource = interactionSource,
                    trailingIcon = {
                        if (state.input.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd) // Keep button at the top // Adj
                                    .padding(top = 0.dp), // Adjust if needed
                                contentAlignment = Alignment.TopEnd
                            ) {
                                IconButton(onClick = { viewModel.onInputChange("") } ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.trailingicon),
                                        contentDescription = "Clear text"
                                    )
                                }
                            }
                        }
                    }
                )
            }
            // Save button
            androidx.compose.material3.Button(
                onClick = { viewModel.save() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                androidx.compose.material3.Text(text = if (state.isLoading) "Processing…" else "Save")
            }

            Spacer(modifier = Modifier.height(33.dp))
            if (state.isLoading) {
                SvgCircularLoaderWithLogo(
                    svgResId = R.drawable.disclimerlogo,
                    modifier = Modifier.size(80.dp)
                )
            }

            if (state.preferences.isEmpty() && !state.isLoading) {
                Demo()
            } else if (!state.isLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.preferences, key = { it.id }) { pref ->
                        Text(
                            text = pref.preferenceText,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}

// In Demo, fix PrimarayGreen50 to PrimaryGreen50
@Composable
fun Demo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.emptystateillustration),
            contentDescription = "Your image",
            modifier = Modifier
                .width(201.dp)
                .height(180.dp)
                .alpha(1.0f)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(104.dp))
        Text(
            text = "Try the following",
            color = Greyscale500,
            modifier = Modifier
                .width(120.dp)
                .height(21.dp)
                .align(Alignment.CenterHorizontally),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.32).sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                        .padding(horizontal = 8.dp)
                        .background(
                            color = PrimarayGreen50,
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 8.dp,
                                bottomEnd = 8.dp,
                                bottomStart = 8.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val annotatedText = when (page) {
                        0 -> buildAnnotatedString {
                            append("I follow a ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("vegetarian")
                            }
                            append(" diet, but I'm okay with eating ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("fish")
                            }
                            append(".")
                        }

                        1 -> buildAnnotatedString {
                            append("Scan ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("food ingredients")
                            }
                            append(" easily.")
                        }

                        2 -> buildAnnotatedString {
                            append("Get alerts on ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("dietary restrictions")
                            }
                            append(".")
                        }

                        else -> buildAnnotatedString { append("") }
                    }
                    Text(
                        text = annotatedText,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 15.sp,
                        color = PrimaryGreen100
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) PrimaryGreen100 else Greyscale200,
                                shape = CircleShape
                            )
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}


@Composable
fun SvgCircularLoaderWithLogo(
    svgResId: Int, // Your SVG logo from drawable
    barCount: Int = 8,
    barWidth: Dp = 2.dp,
    barHeight: Dp = 18.dp,
    minScale: Float = 0.5f,
    maxScale: Float = 1f,
    radius: Dp = 16.dp,
    color: Color = Color(0xFF448AFF),
    durationMillis: Int = 1000,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LoaderAnimation")
    val animations = List(barCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = minScale,
            targetValue = maxScale,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    this.durationMillis=durationMillis
                    minScale at (index * durationMillis / barCount)
                    maxScale at (index * durationMillis / barCount + durationMillis / (2 * barCount))
                    minScale at (index * durationMillis / barCount + durationMillis / barCount)
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "Bar$index"
        )
    }

    Box(
        modifier = modifier
            .size((radius * 2) + barHeight)
            .wrapContentSize(Alignment.Center)
    ) {
        // Center SVG Logo
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(svgResId)
                    .decoderFactory(SvgDecoder.Factory())
                    .build()
            ),
            contentDescription = "Logo",
            modifier = Modifier.size(32.dp) // Adjust size of your logo
        )

        // Canvas to draw rotating bars
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val angleStep = 360f / barCount
            val radiusPx = radius.toPx()
            val barWidthPx = barWidth.toPx()

            for (i in 0 until barCount) {
                val scale = animations[i].value
                val angle = Math.toRadians((i * angleStep).toDouble())
                val x = center.x + radiusPx * cos(angle).toFloat()
                val y = center.y + radiusPx * sin(angle).toFloat()

                val barHeightPx = barHeight.toPx() * scale

                drawRoundRect(
                    color = color,
                    topLeft = Offset(
                        x - barWidthPx / 2,
                        y - barHeightPx / 2
                    ),
                    size = Size(barWidthPx, barHeightPx),
                    cornerRadius = CornerRadius(barWidthPx / 2, barWidthPx / 2),
                    alpha = scale,
                    // rotate around center of the bar
                    style = Fill
                )
            }
        }
    }
}

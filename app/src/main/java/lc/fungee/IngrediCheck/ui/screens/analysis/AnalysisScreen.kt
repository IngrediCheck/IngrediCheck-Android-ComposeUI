package lc.fungee.IngrediCheck.ui.screens.analysis

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.data.model.DecoratedIngredientListFragment
import lc.fungee.IngrediCheck.data.model.ImageInfo
import lc.fungee.IngrediCheck.data.model.Ingredient
import lc.fungee.IngrediCheck.data.model.IngredientRecommendation
import lc.fungee.IngrediCheck.data.model.Product
import lc.fungee.IngrediCheck.data.model.ProductRecommendation
import lc.fungee.IngrediCheck.data.model.SafetyRecommendation
import lc.fungee.IngrediCheck.data.model.calculateMatch
import lc.fungee.IngrediCheck.data.repository.AnalysisPhase
import lc.fungee.IngrediCheck.data.repository.AnalysisViewModel
import lc.fungee.IngrediCheck.data.repository.ListTabRepository
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import lc.fungee.IngrediCheck.data.source.hapticSuccess
import lc.fungee.IngrediCheck.data.source.image.ImageCache
import lc.fungee.IngrediCheck.data.source.image.rememberResolvedImageModel
import lc.fungee.IngrediCheck.ui.screens.feedback.FeedbackMode
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.ui.theme.StatusUnmatchedBg
import lc.fungee.IngrediCheck.ui.theme.StatusUnmatchedFg
import lc.fungee.IngrediCheck.ui.theme.StatusMaybeBg
import lc.fungee.IngrediCheck.ui.theme.StatusUncertainFg
import lc.fungee.IngrediCheck.ui.theme.TooltipBg
import lc.fungee.IngrediCheck.ui.theme.SafetySafeBg
import lc.fungee.IngrediCheck.ui.theme.SafetyMaybeUnsafeBg
import lc.fungee.IngrediCheck.ui.theme.SafetyDefinitelyUnsafeBg
import lc.fungee.IngrediCheck.ui.theme.SafetyNoneBg
import lc.fungee.IngrediCheck.ui.theme.StatusSafeIcon
import lc.fungee.IngrediCheck.ui.theme.StatusMaybeUnsafeIcon
import lc.fungee.IngrediCheck.ui.theme.StatusDefinitelyUnsafeIcon
import lc.fungee.IngrediCheck.ui.theme.StatusNoneIcon
import lc.fungee.IngrediCheck.ui.theme.Greyscale50
import lc.fungee.IngrediCheck.ui.theme.Greyscale600
import lc.fungee.IngrediCheck.ui.theme.StatusMatchBg
import lc.fungee.IngrediCheck.ui.theme.StatusMatchFg
import lc.fungee.IngrediCheck.ui.theme.StatusUncertainBg
import lc.fungee.IngrediCheck.ui.theme.White
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    barcode: String?,
    images: List<ImageInfo>? = null,
    supabaseClient: SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    onRetakeRequested: () -> Unit = {},
    onBackToScanner: () -> Unit = {},
    onOpenFeedback: (mode: FeedbackMode, clientActivityId: String) -> Unit = { _, _ -> }
) {
    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current

    // Use the existing authenticated Supabase client
    val preferenceRepository =
        remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(preferenceRepository, functionsBaseUrl, anonKey) }
    val viewModel = remember { AnalysisViewModel(preferenceRepository, functionsBaseUrl, anonKey) }
    val scope = rememberCoroutineScope()
    val clientActivityId = remember(barcode, images) { UUID.randomUUID().toString() }
    LaunchedEffect(barcode, images) {
        if (!images.isNullOrEmpty()) {
            viewModel.analyzeImages(clientActivityId, images)
        } else if (!barcode.isNullOrBlank()) {
            viewModel.analyzeBarcode(clientActivityId, barcode!!)
        }
    }

    LazyColumn(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .fillMaxHeight(0.94f)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        // Top section: loading/error/product header
        item {
            val loadingLabel = when {
                !images.isNullOrEmpty() -> "Analysing Image..."
                !barcode.isNullOrBlank() -> "Looking up $barcode"
                else -> "Error :The request timed out.:"
            }
            when {
                viewModel.phase == AnalysisPhase.LoadingProduct && viewModel.product == null -> LoadingContent(
                    loadingLabel
                )

                viewModel.product != null -> {
                    hapticSuccess(haptic)
                    val result = viewModel.product!!.calculateMatch(viewModel.recommendations)
                    ProductHeader(
                        viewModel.product!!,
                        result,
                        viewModel.phase,
                        viewModel.recommendations,
                        supabaseClient,
                        onToggleFavorite = { newValue ->
                            val caid = clientActivityId
                            scope.launch {
                                val ok = runCatching {
                                    if (newValue) listRepo.addToFavorites(caid) else listRepo.removeFromFavoritesByClientActivity(
                                        caid
                                    )
                                }.getOrDefault(false)
                                // no-op on failure; UI will flip back inside header
                            }
                        },
                        onRetakeRequested = onRetakeRequested,
                        onBackToScanner = onBackToScanner,
                        onFlagClick = {
                            val mode =
                                if (images.isNullOrEmpty()) FeedbackMode.FeedbackAndImages else FeedbackMode.FeedbackOnly
                            onOpenFeedback(mode, clientActivityId)
                        },
                        onUploadPhotosClick = {
                            onOpenFeedback(FeedbackMode.ImagesOnly, clientActivityId)
                        }
                    )
                    Spacer(Modifier.Companion.height(16.dp))
                }

                viewModel.error != null -> ErrorContent(
                    message = viewModel.error!!,
                    onRetry = {
                        val idRetry = UUID.randomUUID().toString()
                        if (!images.isNullOrEmpty()) {
                            viewModel.analyzeImages(idRetry, images)
                        } else if (!barcode.isNullOrBlank()) {
                            viewModel.analyzeBarcode(idRetry, barcode!!)
                        }
                    }
                )

                else -> LoadingContent(loadingLabel)
            }
        }

        // Ingredients header and list items rendered in the same LazyColumn
        if (viewModel.product != null) {
            if (viewModel.recommendations.isNotEmpty()) {
//                item {
////                    Column(Modifier.padding(horizontal = 16.dp)) {
////                        Text(
////                            text = "Ingredient Safety Analysis",
////                            fontSize = 20.sp,
////                            fontWeight = FontWeight.Bold,
////                            modifier = Modifier.padding(bottom = 16.dp)
////                        )
////                    }
//                }
                items(viewModel.recommendations) { recommendation ->
                    // Reuse existing item UI
//                    IngredientSafetyItem(recommendation)
                }
            } else {
                ""
            }
        }
    }
}

@Composable
fun ProductHeader(
    product: Product,
    result: ProductRecommendation,
    phase: AnalysisPhase,
    recommendations: List<IngredientRecommendation>,
    supabaseClient: SupabaseClient,
    onToggleFavorite: (Boolean) -> Unit = {},
    onRetakeRequested: () -> Unit = {},
    onBackToScanner: () -> Unit = {},
    onFlagClick: () -> Unit = {},
    onUploadPhotosClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(White)
    ) {

        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            val actionIconModifier = Modifier.Companion.size(24.dp)
            Icon(
//                painter = painterResource(id = R.drawable.backbutton),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "back button",
                modifier = actionIconModifier.clickable { onBackToScanner() },
                tint = AppColors.Brand
            )
            Spacer(modifier = Modifier.Companion.weight(0.9f)) // pushes the next items to the end

            var isLike by remember { mutableStateOf(false) }
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Check again",
                modifier = actionIconModifier.clickable { onRetakeRequested() },
                tint = AppColors.Brand
            )

            Spacer(modifier = Modifier.Companion.width(16.dp))

            Icon(
                imageVector = if (isLike) Icons.Default.Favorite
                else Icons.Default.FavoriteBorder,
//
                contentDescription = "Heart logo",
                modifier = actionIconModifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    val newVal = !isLike
                    isLike = newVal
                    onToggleFavorite(newVal)
                },
                tint = if (isLike) Color.Companion.Red else AppColors.Brand
            )

            Spacer(modifier = Modifier.Companion.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.flaglogo),
                contentDescription = "Flag logo",
                modifier = actionIconModifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() } //
                ) {
                    onFlagClick()
                },
                tint = Color.Companion.Unspecified //
            )

        }
        // Product Name
        Text(
            text = product.name ?: "Unknown Product",
            fontSize = 17.sp, // as per your spec (≈ 17px)
            fontWeight = FontWeight.Companion.SemiBold, // closest to weight 590
            lineHeight = 22.sp,
            letterSpacing = (-0.41).sp,
            color = Color.Companion.Black,
            textAlign = TextAlign.Companion.Center,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )


        // Brand (optional)
        product.brand?.let { brand ->
            Text(
                text = brand,
                fontSize = 15.sp,
                color = Color.Companion.Gray,
                textAlign = TextAlign.Companion.Center,
                modifier = Modifier.Companion
                    .fillMaxWidth() // make it span the whole width
                    .padding(top = 4.dp)
            )
        }


        val pagerState = rememberPagerState()
        val totalPages = product.images.size + 1 // extra blank page
        val ctx = LocalContext.current

        Column(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            // Pager
            HorizontalPager(
                count = totalPages,
                state = pagerState,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .background(Greyscale50)
                    .height(350.dp) // fixed frame height
            ) { page ->
                if (page < product.images.size) {
                    val img = product.images[page]
                    // Prefer direct URL if present; else load cached MEDIUM by hash
                    val directUrl = img.url ?: img.imageUrl
                    val modelState =
                        rememberResolvedImageModel(img, supabaseClient, ImageCache.Size.MEDIUM)
                    val model = modelState.value
                    if (model != null) {
                        AsyncImage(
                            model = model,
                            contentDescription = product.name,
                            modifier = Modifier.Companion.fillMaxSize(0.8f),
                            contentScale = ContentScale.Companion.Fit
                        )
                    } else {
                        // Placeholder when image cannot be resolved yet
                        Box(
                            modifier = Modifier.Companion
                                .fillMaxSize()
                                .background(Color.Companion.LightGray)
                        )
                    }
                } else {
                    // Last page: blank gray background (Upload Photos tile)
                    Box(
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .background(Color.Companion.LightGray)
                            .clickable { onUploadPhotosClick() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clickimageplaceholder),
                            contentDescription = "click image",
                            modifier = Modifier.Companion
                                .width(220.dp) // set custom width
                                .height(220.dp)
                                .align(alignment = Alignment.Companion.Center) // set custom height
                        )
                    }
                }
            }

            // Pager Indicator (dots)
            DynamicPagerIndicator(
                currentPage = pagerState.currentPage,
                totalPages = product.images.size + 1, // include blank page
                modifier = Modifier.Companion.padding(10.dp)
            )

            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AnalysisResultSection(
                    product = product,
                    recommendations = recommendations,
                    phase = phase
                )
            }
        }
    }
}

data class StatusUi(
    val bg: Color,
    val fg: Color,
    val label: String,
    val iconRes: Int
)

@Composable
fun AnalysisStatusChip(
    phase: AnalysisPhase,
    result: ProductRecommendation? = null
) {
    val chipModifier = Modifier.Companion
        .fillMaxWidth()
        .height(64.dp)
        .clip(RoundedCornerShape(8.dp)) // border-radius = 8
        .padding(horizontal = 16.dp, vertical = 16.dp)

    if (phase == AnalysisPhase.Analyzing) {
        val bg = AppColors.BrandLight
        val fg = AppColors.Brand

        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(64.dp)

                .background(bg)
                .padding(vertical = 16.dp, horizontal = 16.dp), // top/bottom padding
            contentAlignment = Alignment.Companion.Center
        ) {
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    color = fg,
                    strokeWidth = 2.dp,
                    modifier = Modifier.Companion.size(20.dp)
                )
                Text(
                    text = "Analyzing",
                    color = fg,
                    fontWeight = FontWeight.Companion.Bold,
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                    letterSpacing = 0.38.sp,
                    textAlign = TextAlign.Companion.Center
                )
            }
        }
        return
    }


    if (result != null) {
        val (bg, fg, label, icons) = when (result) {
            ProductRecommendation.Match -> StatusUi(
                StatusMatchBg,
                StatusMatchFg,
                "Matched",
                R.drawable.matched
            )

            ProductRecommendation.NeedsReview -> StatusUi(
                StatusUncertainBg,
                StatusUncertainFg,
                "Uncertain",
                R.drawable.uncertian
            )

            ProductRecommendation.NotMatch -> StatusUi(
                StatusUnmatchedBg,
                StatusUnmatchedFg,
                "Unmatched",
                R.drawable.unmatched
            )
        }

        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(64.dp)

                .background(bg)
                .padding(vertical = 16.dp, horizontal = 16.dp), // top/bottom padding
            contentAlignment = Alignment.Companion.Center
        ) {
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = icons), contentDescription = "",
                    modifier = Modifier.Companion.size(27.dp), tint = Color.Companion.Unspecified
                )


                Text(
                    text = label,
                    color = fg,
                    fontWeight = FontWeight.Companion.Bold, // 700
                    fontSize = 20.sp, // Figma size
                    lineHeight = 25.sp,
                    letterSpacing = 0.38.sp,
                    textAlign = TextAlign.Companion.Center
                )
            }
        }
    }
}


@Composable
fun DecoratedIngredientsText(
    fragments: List<DecoratedIngredientListFragment>,
    recommendations: List<IngredientRecommendation> = emptyList()
) {
    val annotated: AnnotatedString = buildAnnotatedString {
        fragments.forEach { f ->
            val start = length
            append(f.fragment)
            val end = length

            val isUnmatched = f.preference.isNullOrBlank()
            val style = when {
                f.safetyRecommendation == SafetyRecommendation.DefinitelyUnsafe ->
                    SpanStyle(
                        background = StatusUnmatchedBg,
                        color = StatusUnmatchedFg,
                        fontWeight = FontWeight.Companion.SemiBold
                    )
                // If API says MaybeUnsafe but user preference didn't match this ingredient, escalate to red style
                f.safetyRecommendation == SafetyRecommendation.MaybeUnsafe && isUnmatched ->
                    SpanStyle(
                        background = StatusUnmatchedBg,
                        color = StatusUnmatchedFg,
                        fontWeight = FontWeight.Companion.SemiBold
                    )

                f.safetyRecommendation == SafetyRecommendation.MaybeUnsafe ->
                    SpanStyle(
                        background = StatusMaybeBg,
                        color = StatusUncertainFg,
                        fontWeight = FontWeight.Companion.SemiBold
                    )

                else ->
                    SpanStyle(color = Greyscale600)
            }
            addStyle(style, start, end)

            // Add clickable annotation directly from decorated fragment metadata.
            // This lets us show reasoning + which preference and matched/unmatched state.
            if (f.safetyRecommendation == SafetyRecommendation.DefinitelyUnsafe ||
                f.safetyRecommendation == SafetyRecommendation.MaybeUnsafe
            ) {

                val pref = f.preference
//
                addStringAnnotation(
                    tag = "TOOLTIP",
                    annotation = pref.toString(),
                    start = start,
                    end = end
                )
            }
        }
    }

    var showTooltip by remember { mutableStateOf(false) }
    var tooltipText by remember { mutableStateOf("") }
    var tapOffsetPx by remember { mutableStateOf(IntOffset.Companion.Zero) }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures {
                    // If tapping outside tooltip/text → dismiss
                    if (showTooltip) showTooltip = false
                }
            }
    ) {
        val density = LocalDensity.current
        ClickableText(
            text = annotated,
            style = LocalTextStyle.current.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Companion.Normal,
                lineHeight = 22.5.sp,
                letterSpacing = (-0.24).sp,
                textAlign = TextAlign.Companion.Start
            ),
            modifier = Modifier.Companion.fillMaxWidth(),
            onTextLayout = { layoutResult = it },
            onClick = { offset ->
                annotated.getStringAnnotations("TOOLTIP", offset, offset)
                    .firstOrNull()?.let { span ->
                        tooltipText = span.item
                        showTooltip = true
                        // Store tap position in PX relative to anchor for robust Popup positioning
                        layoutResult?.let { layout ->
                            val rect = layout.getBoundingBox(offset)
                            // Use the horizontal center of the tapped span and its top as the anchor
                            val centerX = rect.left + (rect.width / 2f)
                            val topY = rect.top

                            with(density) {
                                tapOffsetPx = IntOffset(
                                    centerX.toInt(),
                                    topY.toInt()
                                )
                            }
                        }
                    }
            }
        )

        if (showTooltip) {
            // Use a clamped Popup so it never renders off-screen. It will try to show above the tap;
            // if not enough space, it will flip below.
            val provider = remember(tapOffsetPx) {
                object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize
                    ): IntOffset {
                        val padding = 12
                        // Prefer above the tapped position
                        var x = anchorBounds.left + tapOffsetPx.x - popupContentSize.width / 2
                        var y = anchorBounds.top + tapOffsetPx.y - popupContentSize.height - 8

                        // If above doesn't fit, place below
                        if (y < padding) {
                            y = (anchorBounds.top + tapOffsetPx.y + 8).coerceAtMost(
                                windowSize.height - popupContentSize.height - padding
                            )
                        }

                        // Clamp horizontally
                        x = x.coerceIn(padding, windowSize.width - popupContentSize.width - padding)
                        // Final vertical clamp
                        y = y.coerceIn(
                            padding,
                            windowSize.height - popupContentSize.height - padding
                        )

                        return IntOffset(x, y)
                    }
                }
            }

            Popup(
                popupPositionProvider = provider,
                onDismissRequest = { showTooltip = false },
                properties = PopupProperties(focusable = false, dismissOnClickOutside = true)
            ) {
                val scrollState = rememberScrollState()

                Box(
                    modifier = Modifier.Companion
                        .background(
                            TooltipBg,
                            androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .widthIn(max = 250.dp) // constrain width
                        .heightIn(max = 200.dp) // constrain height
                ) {
                    Column(
                        modifier = Modifier.Companion.verticalScroll(scrollState) // scrolling enabled
                    ) {
                        Text(
                            text = tooltipText,
                            color = Color.Companion.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun IngredientSafetyItem(recommendation: IngredientRecommendation) {
    val backgroundColor = when (recommendation.safetyRecommendation) {
        SafetyRecommendation.Safe -> SafetySafeBg
        SafetyRecommendation.MaybeUnsafe -> SafetyMaybeUnsafeBg
        SafetyRecommendation.DefinitelyUnsafe -> SafetyDefinitelyUnsafeBg
        SafetyRecommendation.None -> SafetyNoneBg
    }

    val iconColor = when (recommendation.safetyRecommendation) {
        SafetyRecommendation.Safe -> StatusSafeIcon
        SafetyRecommendation.MaybeUnsafe -> StatusMaybeUnsafeIcon
        SafetyRecommendation.DefinitelyUnsafe -> StatusDefinitelyUnsafeIcon
        SafetyRecommendation.None -> StatusNoneIcon
    }

    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.Companion.padding(16.dp),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Safety Level",
                tint = iconColor,
                modifier = Modifier.Companion.size(24.dp)
            )
            Spacer(modifier = Modifier.Companion.width(12.dp))
            Column(modifier = Modifier.Companion.weight(1f)) {
                Text(
                    text = recommendation.ingredientName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Companion.Bold
                )
                Text(
                    text = recommendation.reasoning,
                    fontSize = 14.sp,
                    modifier = Modifier.Companion.padding(top = 4.dp)
                )
                Text(
                    text = "Preference: ${recommendation.preference}",
                    fontSize = 12.sp,
                    color = Color.Companion.Gray,
                    modifier = Modifier.Companion.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DynamicPagerIndicator(
    currentPage: Int,
    totalPages: Int, // includes +1 extra blank page
    modifier: Modifier = Modifier.Companion
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        val visibleDots = when {
            totalPages <= 2 -> totalPages // for 1 image → 2 dots
            currentPage <= 1 -> 0
            currentPage >= totalPages - 2 -> 3 // near the end → show last 3
            else -> 3 // middle → sliding window
        }

        val startIndex = when {
            totalPages <= 2 -> 0
            currentPage <= 1 -> 0
            currentPage >= totalPages - 2 -> totalPages - visibleDots
            else -> currentPage - 1
        }

        for (pageIndex in startIndex until (startIndex + visibleDots)) {
            val isSelected = pageIndex == currentPage

            val dotSize by animateDpAsState(
                targetValue = if (isSelected) 12.dp else 8.dp,
                label = "DotSizeAnim"
            )
            val dotColor by animateColorAsState(
                targetValue = AppColors.Brand,
                label = "DotColorAnim"
            )

            Box(
                modifier = Modifier.Companion
                    .padding(horizontal = 4.dp)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

@Composable
fun LoadingContent(barcode: String) {

    Box(
        modifier = Modifier.Companion.fillMaxSize(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {

            CircularProgressIndicator()

            Spacer(modifier = Modifier.Companion.height(16.dp))
            Text("Looking up $barcode", color = Color.Companion.Black)
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {


    Box(
        modifier = Modifier.Companion.fillMaxSize(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = Color.Companion.Red,
                modifier = Modifier.Companion.size(64.dp)
            )
            Spacer(modifier = Modifier.Companion.height(16.dp))
            Text(
                text = "Something went wrong",
                fontSize = 18.sp,
                fontWeight = FontWeight.Companion.Bold
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.Companion.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.Companion.height(24.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

fun flattenIngredients(ingredients: List<Ingredient>?): List<String> {
    if (ingredients == null) return emptyList()
    val result = mutableListOf<String>()
    for (ingredient in ingredients) {
        ingredient.name?.let { result.add(it) }
        result.addAll(flattenIngredients(ingredient.ingredients))
    }
    return result
}
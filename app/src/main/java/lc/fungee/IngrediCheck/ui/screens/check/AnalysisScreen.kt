package lc.fungee.IngrediCheck.ui.screens.check

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.data.model.*
import lc.fungee.IngrediCheck.data.repository.AnalysisViewModel
import lc.fungee.IngrediCheck.data.repository.AnalysisPhase
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import lc.fungee.IngrediCheck.data.repository.ListTabRepository
import java.util.UUID
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import lc.fungee.IngrediCheck.R

import lc.fungee.IngrediCheck.data.model.IngredientRecommendation
import lc.fungee.IngrediCheck.data.model.ImageInfo
import lc.fungee.IngrediCheck.data.source.hapticSuccess
import lc.fungee.IngrediCheck.ui.theme.Greyscale600
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100
import lc.fungee.IngrediCheck.ui.theme.White
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontFamily
import lc.fungee.IngrediCheck.ui.theme.Greyscale50
import java.nio.file.WatchEvent
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import lc.fungee.IngrediCheck.ui.screens.analysis.AnalysisResultSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    barcode: String?,
    images: List<ImageInfo>? = null,
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String
) {

    val context = androidx.compose.ui.platform.LocalContext.current

    // Use the existing authenticated Supabase client
    val preferenceRepository = remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.94f)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top section: loading/error/product header
        item {
            val loadingLabel = if (!images.isNullOrEmpty()) "label images" else (barcode ?: "")
            when {
                viewModel.phase == AnalysisPhase.LoadingProduct && viewModel.product == null -> LoadingContent(
                    loadingLabel
                )

                viewModel.product != null -> {
                    hapticSuccess(context)
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
                                    if (newValue) listRepo.addToFavorites(caid) else listRepo.removeFromFavoritesByClientActivity(caid)
                                }.getOrDefault(false)
                                // no-op on failure; UI will flip back inside header
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
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
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    onToggleFavorite: (Boolean) -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val actionIconModifier = Modifier.size(24.dp)
            Icon(
//                painter = painterResource(id = R.drawable.backbutton),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "back button",
                modifier = actionIconModifier.clickable { /*TODO*/ },
                tint = PrimaryGreen100
            )
            Spacer(modifier = Modifier.weight(0.9f)) // pushes the next items to the end
            var isLike by remember { mutableStateOf(false) }
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
                tint = if (isLike) Color.Red else PrimaryGreen100
            )

            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.flaglogo),
                contentDescription = "Flag logo",
                modifier = actionIconModifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() } // 
                ) {
                    isLike = !isLike
                },
                tint = Color.Unspecified // 
            )

        }
        // Product Name
        Text(
            text = product.name ?: "Unknown Product",
            fontSize = 17.sp, // as per your spec (≈ 17px)
            fontWeight = FontWeight.SemiBold, // closest to weight 590
            lineHeight = 22.sp,
            letterSpacing = (-0.41).sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )


        // Brand (optional)
        product.brand?.let { brand ->
            Text(
                text = brand,
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth() // make it span the whole width
                    .padding(top = 4.dp)
            )
        }


        val pagerState = rememberPagerState()
        val totalPages = product.images.size + 1 // extra blank page

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager
            HorizontalPager(
                count = totalPages,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Greyscale50)
                    .height(350.dp) // fixed frame height
            ) { page ->
                if (page < product.images.size) {
                    val img = product.images[page]
                    // Resolve URL for either direct URL or a Supabase Storage hash
                    val imageUrl by produceState<String?>(initialValue = img.url ?: img.imageUrl, key1 = img) {
                        if ((value.isNullOrBlank()) && img.imageFileHash != null) {
                            val bucket = supabaseClient.storage.from("productimages")
                            // Prefer a signed URL to work with private buckets
                            value = try {
                                bucket.createSignedUrl(img.imageFileHash!!, 3600.seconds)
                            } catch (e: Exception) {
                                null
                            }
                            // Fallback to public URL only if bucket is public
                            if (value.isNullOrBlank()) {
                                value = try {
                                    bucket.publicUrl(img.imageFileHash!!)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }
                    }

                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(0.8f),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Placeholder when image URL cannot be resolved
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray)
                        )
                    }
                } else {
                    // Last page: blank gray background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clickimageplaceholder),
                            contentDescription = "click image",
                            modifier = Modifier
                                .width(220.dp) // set custom width
                                .height(220.dp)
                                .align(alignment = Alignment.Center) // set custom height
                        )
                    }
                }
            }

            // Pager Indicator (dots)
            DynamicPagerIndicator(
                currentPage = pagerState.currentPage,
                totalPages = product.images.size + 1, // include blank page
                modifier = Modifier.padding(10.dp)
            )

            Column(
                modifier = Modifier
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
    val chipModifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .clip(RoundedCornerShape(8.dp)) // border-radius = 8
        .padding(horizontal = 16.dp, vertical = 16.dp)

    if (phase == AnalysisPhase.Analyzing) {
        val bg = Color(0xFFF6FCEE)
        val fg = Color(0xFF789D0E)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)

                .background(bg)
                .padding(vertical = 16.dp, horizontal = 16.dp), // top/bottom padding
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    color = fg,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Analyzing",
                    color = fg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                    letterSpacing = 0.38.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }


    if (result != null) {
        val (bg, fg, label, icons) = when (result) {
            ProductRecommendation.Match -> StatusUi(
                Color(0xFFF3FFF7),
                Color(0xFF047D4B),
                "Matched",
                R.drawable.matched
            )

            ProductRecommendation.NeedsReview -> StatusUi(
                Color(0xFFFFFBF0),
                Color(0xFF955102),
                "Uncertain",
                R.drawable.uncertian
            )

            ProductRecommendation.NotMatch -> StatusUi(
                Color(0xFFFFF5F4),
                Color(0xFF972D26),
                "Unmatched",
                R.drawable.unmatched
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)

                .background(bg)
                .padding(vertical = 16.dp, horizontal = 16.dp), // top/bottom padding
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = icons), contentDescription = "",
                    modifier = Modifier.size(27.dp), tint = Color.Unspecified
                )


                Text(
                    text = label,
                    color = fg,
                    fontWeight = FontWeight.Bold, // 700
                    fontSize = 20.sp, // Figma size
                    lineHeight = 25.sp,
                    letterSpacing = 0.38.sp,
                    textAlign = TextAlign.Center
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
                        background = Color(0xFFFFF5F4),
                        color = Color(0xFF972D26),
                        fontWeight = FontWeight.SemiBold
                    )
                // If API says MaybeUnsafe but user preference didn't match this ingredient, escalate to red style
                f.safetyRecommendation == SafetyRecommendation.MaybeUnsafe && isUnmatched ->
                    SpanStyle(
                        background = Color(0xFFFFF5F4),
                        color = Color(0xFF972D26),
                        fontWeight = FontWeight.SemiBold
                    )

                f.safetyRecommendation == SafetyRecommendation.MaybeUnsafe ->
                    SpanStyle(
                        background = Color(0xFFFFF9EA),
                        color = Color(0xFF955102),
                        fontWeight = FontWeight.SemiBold
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
    var tapOffsetPx by remember { mutableStateOf(IntOffset.Zero) }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Box(
        modifier = Modifier
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
                fontWeight = FontWeight.Normal,
                lineHeight = 22.5.sp,
                letterSpacing = (-0.24).sp,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth(),
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
                        anchorBounds: androidx.compose.ui.unit.IntRect,
                        windowSize: androidx.compose.ui.unit.IntSize,
                        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                        popupContentSize: androidx.compose.ui.unit.IntSize
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
                    modifier = Modifier
                        .background(Color(0xFF212121), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .widthIn(max = 250.dp) // constrain width
                        .heightIn(max = 200.dp) // constrain height
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState) // scrolling enabled
                    ) {
                        Text(
                            text = tooltipText,
                            color = Color.White,
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
        SafetyRecommendation.Safe -> Color(0xFFE8F5E8) // Light green
        SafetyRecommendation.MaybeUnsafe -> Color(0xFFFFF3E0) // Light orange
        SafetyRecommendation.DefinitelyUnsafe -> Color(0xFFFFEBEE) // Light red
        SafetyRecommendation.None -> Color(0xFFF5F5F5) // Light gray
    }

    val iconColor = when (recommendation.safetyRecommendation) {
        SafetyRecommendation.Safe -> Color(0xFF4CAF50) // Green
        SafetyRecommendation.MaybeUnsafe -> Color(0xFFFF9800) // Orange
        SafetyRecommendation.DefinitelyUnsafe -> Color(0xFFF44336) // Red
        SafetyRecommendation.None -> Color(0xFF9E9E9E) // Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Safety Level",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.ingredientName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = recommendation.reasoning,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Preference: ${recommendation.preference}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DynamicPagerIndicator(
    currentPage: Int,
    totalPages: Int, // includes +1 extra blank page
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
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
                targetValue = PrimaryGreen100,
                label = "DotColorAnim"
            )

            Box(
                modifier = Modifier
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(16.dp))
            Text("Looking up $barcode", color = Color.Black)
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
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

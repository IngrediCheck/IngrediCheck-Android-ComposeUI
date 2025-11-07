package lc.fungee.IngrediCheck.ui.view.screens.list

import AnalysisResultSection
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.serialization.json.Json
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.model.repository.FavoriteItem
import lc.fungee.IngrediCheck.model.repository.HistoryItem
import lc.fungee.IngrediCheck.model.repository.ListTabRepository
import lc.fungee.IngrediCheck.viewmodel.ListTabViewModel
import lc.fungee.IngrediCheck.model.repository.PreferenceRepository
import lc.fungee.IngrediCheck.model.entities.Product
//import lc.fungee.IngrediCheck.ui.view.screens.analysis.AnalysisResultSection
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.OutlinedTextField
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import lc.fungee.IngrediCheck.ui.view.screens.analysis.DynamicPagerIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalConfiguration
import io.github.jan.supabase.SupabaseClient
import lc.fungee.IngrediCheck.model.entities.Ingredient
import lc.fungee.IngrediCheck.ui.view.component.BottomBar
import lc.fungee.IngrediCheck.ui.view.screens.check.CheckBottomSheet
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackScreen
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackMode
import lc.fungee.IngrediCheck.ui.theme.White
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.ui.theme.BrandDeepGreen
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100
import lc.fungee.IngrediCheck.ui.theme.StatusUncertainFg
import lc.fungee.IngrediCheck.ui.theme.StatusUnmatchedFg
import lc.fungee.IngrediCheck.model.source.image.ImageCache
import lc.fungee.IngrediCheck.model.source.image.rememberResolvedImageModel
import lc.fungee.IngrediCheck.model.entities.SafetyRecommendation
import lc.fungee.IngrediCheck.model.entities.IngredientRecommendation
import lc.fungee.IngrediCheck.model.repository.AnalysisRepository
import java.util.UUID
import java.net.URLEncoder
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.AsyncImagePainter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoritesPageScreen(
    supabaseClient: SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val prefRepo =
        remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(prefRepo, functionsBaseUrl, anonKey) }
    val vm = remember { ListTabViewModel(listRepo) }
    val ui by vm.uiState.collectAsState()

    val pull = rememberPullRefreshState(
        refreshing = ui.isLoadingFavorites,
        onRefresh = { vm.refreshFavorites() }
    )
    var showSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            if (navController != null) BottomBar(
                navController = navController,
                onCheckClick = { showSheet = true })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(paddingValues)
                .pullRefresh(pull)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pull),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Top-left Back row matching ProductHeader style
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val actionIconModifier = Modifier.size(20.dp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { navController?.popBackStack() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.backbutton),
                                contentDescription = "Back",
                                modifier = actionIconModifier.height(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lists",
                                color = PrimaryGreen100,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    Text(
                        text = "Favorites",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = AppColors.Neutral700,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                }

                if (ui.favorites == null && ui.isLoadingFavorites) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) { }
                    }
                } else if (ui.favorites == null) {
                    item { Box(Modifier.fillMaxSize()) }
                } else if (ui.favorites!!.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),

                        ) {
                            Image(
                                painterResource(id = R.drawable.emptyfavimg),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                            Text("No favorite products yet", color = Color.Gray)
                        }
                    }
                } else {
                    items(ui.favorites!!) { item ->
                        FavoriteItemListCard(
                            item = item, supabaseClient = supabaseClient,
                            modifier = Modifier.clickable {
                                val json = URLEncoder.encode(
                                    Json.encodeToString(FavoriteItem.serializer(), item),
                                    "UTF-8"
                                )
                                navController?.navigate("favoriteItem?item=$json")
                            }
                        )
                        Divider(color = AppColors.Divider, thickness = 2.dp)
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = ui.isLoadingFavorites,
                state = pull,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        if (showSheet) {
            CheckBottomSheet(
                onDismiss = { showSheet = false },
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
    }
    // Auto refresh first time Favorites screen opens
    LaunchedEffect(Unit) {
        vm.refreshFavorites()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecentScansPageScreen(
    supabaseClient: SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val prefRepo =
        remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(prefRepo, functionsBaseUrl, anonKey) }
    val vm = remember { ListTabViewModel(listRepo) }
    val ui by vm.uiState.collectAsState()

    var isSearching by remember { mutableStateOf(false) }

    if (isSearching) {
        SearchHistoryView(
            searchText = ui.searchText,
            onSearchTextChange = { vm.setSearchText(it) },
            results = ui.searchResults,
            onBack = { isSearching = false },
            onRefresh = { vm.refreshSearch() },
            onItemClick = { item ->
                val json = URLEncoder.encode(
                    Json.encodeToString(HistoryItem.serializer(), item),
                    "UTF-8"
                )
                navController?.navigate("historyItem?item=$json")
            },
            supabaseClient = supabaseClient
        )
        return
    }

    // ✅ Pull-to-refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = ui.isLoadingHistory,
        onRefresh = { vm.refreshHistory() }
    )

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (navController != null) {
                BottomBar(
                    navController = navController,
                    onCheckClick = { showSheet = true }
                )
            }
        }
    ) { paddingValues ->
        // ✅ Box for pull-to-refresh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Top-left Back row matching ProductHeader style
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val actionIconModifier = Modifier.size(20.dp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { navController?.popBackStack() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.backbutton),
                                contentDescription = "Back",
                                modifier = actionIconModifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lists",
                                color = PrimaryGreen100,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Header
                item {
                    Text(
                        text = "Recent Scans",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = AppColors.Neutral700,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))
                }

                // Loading state
                if (ui.history == null && ui.isLoadingHistory) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }
                }
                // Empty state
                else if (ui.history?.isEmpty() == true) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painterResource(id = R.drawable.emptyrecentscan),
                                contentDescription = null,
                                modifier = Modifier.size(width = 200.dp, height = 150.dp)
                            )
                            Text("No products scanned yet", color = Color.Gray)
                        }
                    }
                }
                // History items
                else if (!ui.history.isNullOrEmpty()) {
                    items(ui.history!!) { item ->
                        HistoryItemCard(
                            item = item,
                            supabaseClient = supabaseClient,
                            modifier = Modifier.clickable {
                                val json = URLEncoder.encode(
                                    Json.encodeToString(HistoryItem.serializer(), item),
                                    "UTF-8"
                                )
                                navController?.navigate("historyItem?item=$json")
                            }
                        )
                        Divider(color = AppColors.Divider, thickness = 2.dp)
                    }
                }
            }

            // ✅ Pull-to-refresh indicator (always overlayed at top)
            PullRefreshIndicator(
                refreshing = ui.isLoadingHistory,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // ✅ Auto refresh first time screen opens
    LaunchedEffect(Unit) {
        vm.refreshHistory()
    }
}


@Composable
fun FavoriteItemDetailScreen(
    itemJson: String,
    supabaseClient: SupabaseClient,
    navController: NavController? = null,
    functionsBaseUrl: String = "",
    anonKey: String = ""
) {
    val item = remember(itemJson) {
        runCatching {
            Json.decodeFromString(
                FavoriteItem.serializer(),
                itemJson
            )
        }.getOrNull()
    }
    val context = LocalContext.current
    val prefRepo = remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(prefRepo, functionsBaseUrl, anonKey) }
    val scope = rememberCoroutineScope()
    val product = remember(item) {
        item?.let {
            Product(
                barcode = it.barcode,
                brand = it.brand,
                name = it.name,
                ingredients = it.ingredients,
                images = it.images
            )
        }
    }
    val analysisRepo = remember { AnalysisRepository(prefRepo, functionsBaseUrl, anonKey) }
    var recs by remember { mutableStateOf<List<IngredientRecommendation>>(emptyList()) }
    LaunchedEffect(item?.barcode) {
        val barcode = item?.barcode
        if (!barcode.isNullOrBlank()) {
            val id = UUID.randomUUID().toString()
            runCatching {
                analysisRepo.fetchRecommendations(
                    clientActivityId = id,
                    prefs = "",
                    barcode = barcode
                )
            }.onSuccess { recs = it }.onFailure { recs = emptyList() }
        } else {
            recs = emptyList()
        }
    }
    var showSheet by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = {
            if (navController != null) BottomBar(
                navController = navController,
                onCheckClick = { showSheet = true })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(paddingValues)
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Header actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val actionIconModifier = Modifier.size(24.dp)
                    Row(
                        modifier = Modifier
                            .clickable { navController?.popBackStack() }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = BrandDeepGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Lists",
                            color = BrandDeepGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.9f))
                    var isLike by remember { mutableStateOf(true) }
                    Icon(
                        imageVector = if (isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Heart logo",
                        modifier = actionIconModifier.clickable {
                            val newVal = !isLike
                            isLike = newVal
                            val listItemId = item?.listItemId
                            if (listItemId != null && !newVal) {
                                scope.launch {
                                    val ok = runCatching { listRepo.removeFavoriteListItem(listItemId) }.getOrDefault(false)
                                    if (!ok) {
                                        isLike = !newVal
                                    } else {
                                        // After removal, go back to the list
                                        navController?.popBackStack()
                                    }
                                }
                            }
                        },
                        tint = if (isLike) Color.Red else BrandDeepGreen
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Title and brand
                Text(
                    text = item?.name ?: "Unknown Product",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    letterSpacing = (-0.41).sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                item?.brand?.let { brand ->
                    Text(
                        text = brand,
                        fontSize = 15.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Images pager (match HistoryItemDetailScreen behavior)
                val pagerState = rememberPagerState(initialPage = 0)
                val imageCount = item?.images?.size ?: 0
                val totalPages = imageCount + 1
                val configuration = LocalConfiguration.current
                val screenWidthDp = configuration.screenWidthDp
                val baseDp = if (screenWidthDp > 0) screenWidthDp else 360
                val pagerHeight = baseDp.dp.coerceIn(240.dp, 420.dp)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPager(
                        count = totalPages,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.SurfaceMuted)
                            .heightIn(min = 240.dp, max = 420.dp)
                            .height(pagerHeight)
                    ) { page ->
                        val imgs = item?.images ?: emptyList()
                        if (page < imgs.size) {
                            val img = imgs[page]
                            val modelState = rememberResolvedImageModel(
                                img, supabaseClient, ImageCache.Size.MEDIUM
                            )
                            val model = modelState.value
                            if (model != null) {
                                SubcomposeAsyncImage(
                                    model = model,
                                    contentDescription = item?.name,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    contentScale = ContentScale.Fit
                                ) {
                                    when (painter.state) {
                                        is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Empty -> {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) { CircularProgressIndicator() }
                                        }
                                        is AsyncImagePainter.State.Error -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.LightGray)
                                            )
                                        }
                                        else -> {
                                            SubcomposeAsyncImageContent()
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.clickimageplaceholder),
                                    contentDescription = "click image",
                                    modifier = Modifier.size(220.dp)
                                )
                            }
                        }
                    }
                    DynamicPagerIndicator(
                        currentPage = pagerState.currentPage.coerceAtMost((imageCount - 1).coerceAtLeast(0)),
                        totalPages = imageCount,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Analysis results and ingredients (match HistoryItemDetailScreen)
                if (product != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        AnalysisResultSection(product = product, recommendations = recs)
                    }
                } else {
                    Text("No ingredient details available", color = Color.Gray)
                }
            }
        }
        if (showSheet) {
            CheckBottomSheet(
                onDismiss = { showSheet = false },
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
    }
}

@Composable
fun HistoryItemDetailScreen(
    itemJson: String,
    supabaseClient: SupabaseClient,
    navController: NavController? = null,
    functionsBaseUrl: String = "",
    anonKey: String = ""
) {
    val item = remember(itemJson) {
        runCatching {
            Json.decodeFromString(
                HistoryItem.serializer(),
                itemJson
            )
        }.getOrNull()
    }
    val context = LocalContext.current
    val prefRepo = remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(prefRepo, functionsBaseUrl, anonKey) }
    val scope = rememberCoroutineScope()
    val product = remember(item) {
        item?.let {
            Product(
                barcode = it.barcode,
                brand = it.brand,
                name = it.name,
                ingredients = it.ingredients,
                images = it.images
            )
        }
    }
    val recs = item?.ingredientRecommendations ?: emptyList()

    var showSheet by remember { mutableStateOf(false) }
    var feedbackMode by remember { mutableStateOf<FeedbackMode?>(null) }
    Scaffold(
        bottomBar = {
            if (navController != null) BottomBar(
                navController = navController,
                onCheckClick = { showSheet = true })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(paddingValues)
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Header actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val actionIconSize = 24.dp
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { navController?.popBackStack() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.backbutton),
                            contentDescription = "Back",
                            modifier = Modifier.size(actionIconSize),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Back",
                            color = PrimaryGreen100,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.9f))
                    var isLike by remember { mutableStateOf(item?.favorited == true) }
                    Icon(
                        painter = painterResource(id = R.drawable.clickimgicon),
                        contentDescription = "Check again",
                        modifier = Modifier.size(actionIconSize).clickable {
                            // Open scanner/check sheet like ProductHeader retake
                            showSheet = true
                        },
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = if (isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Heart logo",
                        modifier = Modifier.size(actionIconSize).clickable {
                            val newVal = !isLike
                            isLike = newVal
                            val caid = item?.clientActivityId
                            if (!caid.isNullOrBlank()) {
                                scope.launch {
                                    val ok = runCatching {
                                        if (newVal) listRepo.addToFavorites(caid) else listRepo.removeFromFavoritesByClientActivity(caid)
                                    }.getOrDefault(false)
                                    if (!ok) isLike = !newVal
                                }
                            }
                        },
                        tint = if (isLike) Color.Red else AppColors.Brand
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.flaglogo),
                        contentDescription = "Flag logo",
                        modifier = Modifier.size(actionIconSize).clickable {
                            // Match ProductHeader: decide feedback mode based on available images
                            val imgs = item?.images ?: emptyList()
                            feedbackMode = if (imgs.isEmpty()) FeedbackMode.FeedbackAndImages else FeedbackMode.FeedbackOnly
                        },
                        tint = Color.Unspecified
                    )
                }

                // Title and brand
                Text(
                    text = item?.name ?: "Unknown Product",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    letterSpacing = (-0.41).sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                item?.brand?.let { brand ->
                    Text(
                        text = brand,
                        fontSize = 15.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Images pager with adaptive height and upload tile
                val pagerState = rememberPagerState(initialPage = 0)
                val imageCount = item?.images?.size ?: 0
                val totalPages = imageCount + 1
                val configuration = LocalConfiguration.current
                val screenWidthDp = configuration.screenWidthDp
                val baseDp = if (screenWidthDp > 0) screenWidthDp else 360
                val pagerHeight = baseDp.dp.coerceIn(240.dp, 420.dp)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPager(
                        count = totalPages,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.SurfaceMuted)
                            .heightIn(min = 240.dp, max = 420.dp)
                            .height(pagerHeight)
                    ) { page ->
                        val imgs = item?.images ?: emptyList()
                        if (page < imgs.size) {
                            val img = imgs[page]
                            val modelState = rememberResolvedImageModel(
                                img, supabaseClient, ImageCache.Size.MEDIUM
                            )
                            val model = modelState.value
                            if (model != null) {
                                SubcomposeAsyncImage(
                                    model = model,
                                    contentDescription = item?.name,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    contentScale = ContentScale.Fit
                                ) {
                                    when (painter.state) {
                                        is AsyncImagePainter.State.Loading, is AsyncImagePainter.State.Empty -> {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) { CircularProgressIndicator() }
                                        }
                                        is AsyncImagePainter.State.Error -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.LightGray)
                                            )
                                        }
                                        else -> {
                                            SubcomposeAsyncImageContent()
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray)
                                    .clickable {
                                        // Open feedback in ImagesOnly mode like ProductHeader
                                        feedbackMode = FeedbackMode.ImagesOnly
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.clickimageplaceholder),
                                    contentDescription = "click image",
                                    modifier = Modifier.size(220.dp)
                                )
                            }
                        }
                    }
                    DynamicPagerIndicator(
                        currentPage = pagerState.currentPage.coerceAtMost((imageCount - 1).coerceAtLeast(0)),
                        totalPages = imageCount,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Analysis result + ingredients
                if (product != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        AnalysisResultSection(product = product, recommendations = recs)
                    }
                } else {
                    Text("No ingredient details available", color = Color.Gray)
                }
            }
        }
        if (showSheet) {
            CheckBottomSheet(
                onDismiss = { showSheet = false },
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
        // Feedback bottom sheet (matches ProductHeader onOpenFeedback behavior)
        val clientActivityId = item?.clientActivityId.orEmpty()
        if (feedbackMode != null && clientActivityId.isNotBlank()) {
            FeedbackScreen(
                mode = feedbackMode!!,
                clientActivityId = clientActivityId,
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey,
                onRequireReauth = {},
                onBack = { feedbackMode = null }
            )
        }
    }
}

private fun flattenNames(ingredient: Ingredient): List<String> {
    val name = ingredient.name?.let { listOf(it) } ?: emptyList()
    return name + ingredient.ingredients.flatMap { flattenNames(it) }
}

@Composable
fun FavoriteItemListCard(
    item: FavoriteItem,
    supabaseClient: SupabaseClient,
    modifier: Modifier = Modifier
) {
    val firstImage = item.images.firstOrNull()
    val modelState = rememberResolvedImageModel(firstImage, supabaseClient, ImageCache.Size.SMALL)
    val model = modelState.value
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(AppColors.SurfaceMuted)
        ) {
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.emptyfavimg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.brand ?: "Unknown Brand", fontSize = 14.sp)
            Text(item.name ?: "Unknown Name", fontSize = 13.sp, color = Color.Gray)
            val rawDate = item.createdAt ?: ""
            val date = if (rawDate.isNotBlank()) {
                try {
                    val parsed = LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME)
                    parsed.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                } catch (e: Exception) {
                    rawDate
                }
            } else ""
            if (date.isNotBlank()) Text(date, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    supabaseClient: SupabaseClient,
    modifier: Modifier = Modifier
) {
    val firstImage = item.images.firstOrNull()
    val modelState = rememberResolvedImageModel(firstImage, supabaseClient, ImageCache.Size.SMALL)
    val model = modelState.value

    // Determine status dot color based on ingredient recommendations
    // Default to Match (green) when there are no Maybe/Definitely flags
    val dotColor = remember(item.ingredientRecommendations) {
        when {
            item.ingredientRecommendations.any { it.safetyRecommendation == SafetyRecommendation.DefinitelyUnsafe } ->
                StatusUnmatchedFg
            item.ingredientRecommendations.any { it.safetyRecommendation == SafetyRecommendation.MaybeUnsafe } ->
                StatusUncertainFg
            else -> AppColors.Brand
        }
    }
    Row(modifier = modifier.fillMaxWidth().padding(end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppColors.SurfaceMuted)
        ) {
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.emptyfavimg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.brand ?: "Unknown Brand", fontSize = 20.sp)
            Text(item.name ?: "Unknown Name", fontSize = 18.sp, color = Color.Gray)
            val rawDate = item.createdAt ?: ""
            val date = if (rawDate.isNotBlank()) {
                try {
                    val parsed = LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME)
                    parsed.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                } catch (e: Exception) {
                    rawDate
                }
            } else ""
            Spacer(modifier =modifier.height(8.dp))

            if (date.isNotBlank()) Text(date, fontSize = 18.sp, color = Color.Gray)
        }

        // Right-side status dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .offset(y = 8.dp)
                .background(dotColor, CircleShape)
        )
    }
}

// Deprecated helper removed; use rememberResolvedImageModel from ImageResolver instead.

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchHistoryView(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    results: List<HistoryItem>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onItemClick: (HistoryItem) -> Unit,
    supabaseClient: SupabaseClient
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pull = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh()
            isRefreshing = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .pullRefresh(pull)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search history") },
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onBack) {
                Text(
                    "Cancel",
                    color = BrandDeepGreen
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (searchText.isNotBlank() && results.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No Results",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(results) { item ->
                    HistoryItemCard(
                        item = item,
                        supabaseClient = supabaseClient,
                        modifier = Modifier.clickable { onItemClick(item) })
                    Divider(color = AppColors.Divider, thickness = 2.dp)
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pull,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

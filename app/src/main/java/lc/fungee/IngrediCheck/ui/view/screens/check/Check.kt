package lc.fungee.IngrediCheck.ui.view.screens.check

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.zIndex
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.activity.compose.BackHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import io.github.jan.supabase.SupabaseClient
import lc.fungee.IngrediCheck.IngrediCheckApp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.ui.theme.LabelsPrimary
import lc.fungee.IngrediCheck.ui.theme.White
import coil.compose.rememberAsyncImagePainter
import lc.fungee.IngrediCheck.ui.view.component.CameraCaptureManager
import lc.fungee.IngrediCheck.ui.view.component.CameraMode
//import lc.fungee.IngrediCheck.ui.view.component.CameraPreview
import lc.fungee.IngrediCheck.ui.view.screens.analysis.AnalysisScreen
import lc.fungee.IngrediCheck.ui.view.screens.analysis.LoadingContent
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackScreen
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackMode
import lc.fungee.IngrediCheck.viewmodel.FeedbackViewModel
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackCaptureSheet
import lc.fungee.IngrediCheck.viewmodel.FeedbackViewModelFactory
import lc.fungee.IngrediCheck.viewmodel.CheckEvent
import lc.fungee.IngrediCheck.viewmodel.CheckUiState
import lc.fungee.IngrediCheck.viewmodel.CheckViewModel
import lc.fungee.IngrediCheck.viewmodel.CheckViewModelFactory
import lc.fungee.IngrediCheck.model.source.hapticSuccess
import lc.fungee.IngrediCheck.ui.view.component.CameraPreview

//@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckBottomSheet(
    onDismiss: () -> Unit,
    supabaseClient: SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sheetContent by remember { mutableStateOf<CheckSheetState>(CheckSheetState.Scanner) }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // ViewModel wiring via factory (manual DI)
    val app = IngrediCheckApp.appInstance
    val checkViewModel: CheckViewModel = viewModel(
        factory = CheckViewModelFactory(
            container = app.container,
            supabaseClient = supabaseClient,
            functionsBaseUrl = functionsBaseUrl,
            anonKey = anonKey,
            appContext = context.applicationContext
        )
    )

    // Also observe uiState as a safety net to drive navigation and errors
    val checkUiState by checkViewModel.uiState.collectAsState()
    LaunchedEffect(checkUiState) {
        when (val s = checkUiState) {
            is CheckUiState.AnalysisReady -> {
                sheetContent = CheckSheetState.Analysis(
                    barcode = s.barcode,
                    images = s.images
                )
            }

            is CheckUiState.Error -> {
                Toast.makeText(context, s.message, Toast.LENGTH_LONG).show()
            }

            else -> Unit
        }
    }

    // Collect one-off events for navigation/toasts; bind to current VM instance
    LaunchedEffect(checkViewModel) {
        checkViewModel.events.collect { event ->
            when (event) {
                is CheckEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_LONG)
                    .show()

                is CheckEvent.NavigateToAnalysis -> {
                    sheetContent = CheckSheetState.Analysis(
                        barcode = event.barcode,
                        images = event.images
                    )
                }
            }
        }
    }

    // Reset VM and local content when sheet is dismissed/hidden
    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Hidden) {
            sheetContent = CheckSheetState.Scanner
            checkViewModel.reset()
        }
    }

    // Feedback nested sheet state (stacked on top of parent sheet)
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackMode by remember { mutableStateOf<FeedbackMode>(FeedbackMode.FeedbackOnly) }
    var feedbackClientActivityId by remember { mutableStateOf("") }
    var showFeedbackCapture by remember { mutableStateOf(false) }

    // Shared Feedback VM for both Feedback sheet and Capture sheet
    val feedbackVm: FeedbackViewModel = viewModel(
        factory = FeedbackViewModelFactory(
            container = app.container,
            supabaseClient = supabaseClient,
            functionsBaseUrl = functionsBaseUrl,
            anonKey = anonKey,
            appContext = context.applicationContext
        )
    )

    // Intercept system back only when Feedback is NOT visible.
    // When Feedback is visible, let its own ModalBottomSheet handle back (so it can delete images on cancel).
    BackHandler(enabled = !showFeedback) {
        when (sheetContent) {
            is CheckSheetState.Analysis -> {
                sheetContent = CheckSheetState.Scanner
            }
            else -> {
                // Dismiss the sheet
                sheetContent = CheckSheetState.Scanner
                checkViewModel.reset()
                onDismiss()
            }
        }
    }

    // Hoisted UI state shared across Scanner and Analysis content
    val selectedItemIndex by checkViewModel.selectedTabIndex.collectAsState()
    var capturedImage by remember { mutableStateOf<File?>(null) }

    ModalBottomSheet(
        onDismissRequest = {
            // Ensure reset on explicit dismiss
            sheetContent = CheckSheetState.Scanner
            checkViewModel.reset()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null,
        shape = RoundedCornerShape(
            topStart = 10.dp,   // top left corner
            topEnd = 10.dp      // top right corner
        )
    ) {

        when (val state = sheetContent) {
            is CheckSheetState.Scanner -> {
                // Tabs
                val tabs: List<String> = listOf("Barcode", "Photo")
                val texts: List<String> =
                    listOf(
                        "Scan Barcode of Package Food item.",
                        "Take Photo of an Ingredient Label."
                    )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.94f)
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp)
                        .verticalScroll(rememberScrollState()),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Camera state used by action row and preview
                    var scannedCode by remember { mutableStateOf<String?>(null) }
                    var showTransition by remember { mutableStateOf(false) }
                    val overlayAlpha by animateFloatAsState(
                        targetValue = if (showTransition) 0.5f else 0f,
                        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                    )
                    // Top action row: Clear (left), Tabs (center), Check (right)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val showActions = selectedItemIndex == 1 && capturedImage != null
                        // Left: Clear (reserve space when hidden)
                        if (showActions) {
                            TextButton(onClick = { capturedImage = null }) {
                                Text("Clear", color = AppColors.Brand, fontSize = 20.sp)
                            }
                        } else {
                            Spacer(Modifier.width(64.dp)) // approx TextButton width placeholder
                        }

                        // Center: Tabs
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TabRow(
                                    selectedTabIndex = selectedItemIndex,
                                    containerColor = Color.LightGray,
                                    modifier = Modifier.clip(RoundedCornerShape(20)),
                                    indicator = { tabPositions ->
                                        if (selectedItemIndex < tabPositions.size) {
                                            Box(
                                                Modifier
                                                    .tabIndicatorOffset(tabPositions[selectedItemIndex])
                                                    .fillMaxHeight()
                                                    .clip(RoundedCornerShape(30))
                                                    .background(AppColors.Brand)
                                                    .zIndex(1f)
                                            )
                                        }
                                    }
                                ) {
                                    tabs.forEachIndexed { index, title ->
                                        Tab(
                                            selected = selectedItemIndex == index,
                                            onClick = { checkViewModel.setSelectedTab(index) },
                                            text = {
                                                Text(
                                                    title,
                                                    color = LabelsPrimary,
                                                    fontSize = 18.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .zIndex(2f)
                                        )
                                    }
                                }
                            }
                        }

                        // Right: Check (reserve space when hidden)
                        if (showActions) {
                            TextButton(
                                onClick = {
                                    capturedImage?.let {
                                        // Immediately switch to Analysis with a loading stub
                                        sheetContent = CheckSheetState.Analysis(barcode = null, images = emptyList())
                                        checkViewModel.onPhotoCaptured(it)
                                    }
                                },
                                enabled = checkUiState !is CheckUiState.Processing
                            ) {
                                Text("Check", color = AppColors.Brand, fontSize = 20.sp)
                            }
                        } else {
                            Spacer(Modifier.width(64.dp))
                        }
                    }

                    // Camera

                    // Clear only when leaving Photo tab; keep preview when switching TO Photo
                    LaunchedEffect(selectedItemIndex) {
                        if (selectedItemIndex == 0) { // Barcode tab
                            capturedImage = null
                        }
                        scannedCode = null
                    }

                    // Lower action row removed; actions are now in the top row with tabs

                    // Show a brief white transition when switching tabs to avoid stale frames feeling
                    LaunchedEffect(selectedItemIndex) {
                        showTransition = true
                        kotlinx.coroutines.delay(240)
                        showTransition = false
                    }

                    // Compute a responsive preview height
                    val config = LocalConfiguration.current
                    val calc = config.screenHeightDp.dp * 0.48f
                    val previewHeight = if (calc < 435.dp) calc else 435.dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(previewHeight)
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Crossfade(targetState = selectedItemIndex, animationSpec = tween(240, easing = FastOutSlowInEasing)) { idx ->
                            CameraPreview(
                                modifier = Modifier.fillMaxSize(),
                                mode = if (idx == 0) CameraMode.Scan else CameraMode.Photo,
                                onPhotoCaptured = { file: File ->
                                    // Normalize EXIF on a background thread so preview is always upright
                                    scope.launch {
                                        val normalized = normalizeImageFile(context, file)
                                        capturedImage = normalized
                                    }
                                },
                                onBarcodeScanned = { value: String? ->
                                    scannedCode = value
                                    if (value != null && value.isNotEmpty()) {
                                        // Delegate navigation to ViewModel event (Scan mode flow)
                                        checkViewModel.onBarcodeScanned(value)
                                    }
                                }
                            )
                        }

                        if (overlayAlpha > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(White.copy(alpha = overlayAlpha))
                            )
                        }
                    }

                    Text(
                        texts[selectedItemIndex],
                        color = LabelsPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 40.dp),
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    if (selectedItemIndex == 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp) // adjust height as needed
                        ) {
                            // Center Capture Button
                            Image(
                                painter = painterResource(id = R.drawable.photobutton),
                                contentDescription = "Photo Button",
                                modifier = Modifier
                                    .align(Alignment.Center) // center of screen
                                    .clip(RoundedCornerShape(25))
                                    .clickable {
                                        if (CameraCaptureManager.isReady()) {
                                            // Bypass haptic on capture tap for immediate feedback
                                            hapticSuccess(haptic, context, useBypass = true)
                                            CameraCaptureManager.takePhoto()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Camera not ready yet",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            )

                            // Left-side Captured Image Preview
                            capturedImage?.let { file ->
                                Image(
                                    painter = rememberAsyncImagePainter(file),
                                    contentDescription = "Captured Photo",
                                    modifier = Modifier
                                        .align(Alignment.CenterStart) // left side, vertically centered
                                        .width(80.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(15.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            is CheckSheetState.Analysis -> {
                // If we navigated with empty args as a preloading sentinel, show a fast loading stub.
                if ((state.images == null || state.images.isEmpty()) && state.barcode.isNullOrBlank()) {
                    LoadingContent(
                        "Preparing analysis...",
                        onBack = {
                            sheetContent = CheckSheetState.Scanner
                            checkViewModel.reset()
                        }
                    )
                } else {
                    AnalysisScreen(
                        barcode = state.barcode,
                        images = state.images,
                        supabaseClient = supabaseClient,
                        functionsBaseUrl = functionsBaseUrl,
                        anonKey = anonKey,
                        onRetakeRequested = {
                            // Go back to scanner; select tab based on what was analyzed
                            sheetContent = CheckSheetState.Scanner
                            val targetTab = when {
                                state.images?.isNotEmpty() == true -> 1 // Photo
                                !state.barcode.isNullOrBlank() -> 0     // Barcode
                                else -> selectedItemIndex               // Fallback: keep current
                            }
                            checkViewModel.setSelectedTab(targetTab)
                        },
                        onBackToScanner = {
                            sheetContent = CheckSheetState.Scanner
                            val targetTab = when {
                                state.images?.isNotEmpty() == true -> 1 // Photo
                                !state.barcode.isNullOrBlank() -> 0     // Barcode
                                else -> selectedItemIndex               // Fallback
                            }
                            checkViewModel.setSelectedTab(targetTab)
                        },
                        onOpenFeedback = { mode, clientActivityId ->
                            feedbackMode = mode
                            feedbackClientActivityId = clientActivityId
                            showFeedback = true
                        }
                    )
                }
            }
        }
    }

    // Stacked Feedback sheet over the Analysis sheet
    if (showFeedback) {
        FeedbackScreen(
            mode = feedbackMode,
            clientActivityId = feedbackClientActivityId,
            supabaseClient = supabaseClient,
            functionsBaseUrl = functionsBaseUrl,
            anonKey = anonKey,
            onRequireReauth = { /* no-op for now */ },
            onBack = { showFeedback = false },
            onOpenCapture = { showFeedbackCapture = true },
            externalVm = feedbackVm
        )
    }

    // Stacked Camera (Capture) sheet over the Feedback sheet
    if (showFeedbackCapture) {
        FeedbackCaptureSheet(
            vm = feedbackVm,
            onDismiss = { showFeedbackCapture = false },
            clientActivityId = feedbackClientActivityId
        )
    }
}

private suspend fun normalizeImageFile(
    context: Context,
    file: File
): File {
    return withContext(Dispatchers.IO) {
        val exif = try {
            ExifInterface(file.absolutePath)
        } catch (_: Exception) {
            null
        }
        val orientation =
            exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                ?: ExifInterface.ORIENTATION_NORMAL

        if (orientation == ExifInterface.ORIENTATION_NORMAL) return@withContext file

        val src: Bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return@withContext file
        val m = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                m.preScale(-1f, 1f); m.postRotate(90f)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                m.preScale(-1f, 1f); m.postRotate(270f)
            }
        }

        val corrected = try {
            Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
        } catch (_: Exception) {
            src
        }
        if (corrected != src && !src.isRecycled) src.recycle()

        val outDir = File(context.cacheDir, "normalized").apply { mkdirs() }
        val outFile = File(outDir, file.nameWithoutExtension + "_norm.jpg")
        outFile.outputStream().use { os ->
            corrected.compress(Bitmap.CompressFormat.JPEG, 95, os)
        }
        if (!corrected.isRecycled) corrected.recycle()
        return@withContext outFile
    }
}

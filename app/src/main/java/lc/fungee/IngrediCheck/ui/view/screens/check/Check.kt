package lc.fungee.IngrediCheck.ui.view.screens.check

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import lc.fungee.IngrediCheck.ui.theme.LabelsPrimary
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.ui.theme.White
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import io.github.jan.supabase.SupabaseClient
import java.io.File
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import lc.fungee.IngrediCheck.IngrediCheckApp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Color
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.view.component.CameraCaptureManager
import lc.fungee.IngrediCheck.ui.view.component.CameraMode
import lc.fungee.IngrediCheck.ui.view.component.CameraPreview
import lc.fungee.IngrediCheck.ui.view.screens.analysis.AnalysisScreen
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackScreen
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackMode
import lc.fungee.IngrediCheck.viewmodel.FeedbackViewModel
import lc.fungee.IngrediCheck.ui.view.screens.feedback.FeedbackCaptureSheet
import lc.fungee.IngrediCheck.viewmodel.FeedbackViewModelFactory
import lc.fungee.IngrediCheck.viewmodel.CheckEvent
import lc.fungee.IngrediCheck.viewmodel.CheckUiState
import lc.fungee.IngrediCheck.viewmodel.CheckViewModel
import lc.fungee.IngrediCheck.viewmodel.CheckViewModelFactory

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
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
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
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Camera state used by action row and preview
                    var scannedCode by remember { mutableStateOf<String?>(null) }
                    // Top action row: Clear (left), Tabs (center), Check (right)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val showActions = selectedItemIndex == 1 && capturedImage != null
                        // Left: Clear (reserve space when hidden)
                        if (showActions) {
                            TextButton(onClick = { capturedImage = null }) {
                                Text("Clear", color = AppColors.Brand)
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
                                    .width(180.dp)
                                    .height(26.dp),
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
                                            onClick = { selectedItemIndex = index },
                                            text = {
                                                Text(
                                                    title,
                                                    color = LabelsPrimary,
                                                    fontSize = 11.sp
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
                                onClick = { capturedImage?.let { checkViewModel.onPhotoCaptured(it) } },
                                enabled = checkUiState !is CheckUiState.Processing
                            ) {
                                Text("Check", color = AppColors.Brand)
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

                    CameraPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(435.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        mode = if (selectedItemIndex == 0) CameraMode.Scan else CameraMode.Photo,
                        onPhotoCaptured = { file ->
                            // Normalize EXIF on a background thread so preview is always upright
                            scope.launch {
                                val normalized = normalizeImageFile(context, file)
                                capturedImage = normalized
                            }
                        }, onBarcodeScanned = { value ->
                            scannedCode = value
                            if (!value.isNullOrEmpty()) {
                                // Delegate navigation to ViewModel event (Scan mode flow)
                                checkViewModel.onBarcodeScanned(value)
                            }
                        }
                    )

                    Text(
                        "${texts[selectedItemIndex]}", color = LabelsPrimary, modifier = Modifier
                            .padding(top = 20.dp, bottom = 40.dp)
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
                        AnalysisScreen(
                            barcode = state.barcode,
                            images = state.images,
                            supabaseClient = supabaseClient,
                            functionsBaseUrl = functionsBaseUrl,
                            anonKey = anonKey,
                            onRetakeRequested = {
                                // Smoothly switch back to the scanner with Photo tab selected
                                sheetContent = CheckSheetState.Scanner
                                selectedItemIndex = 1 // Photo tab
                                // Keep previous capturedImage so preview remains visible in bottom-left
                            },
                            onBackToScanner = {
                                sheetContent = CheckSheetState.Scanner
                                // Keep the previously captured image preview, and remain on the Photo tab
                                selectedItemIndex = 1
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
            onDismiss = { showFeedbackCapture = false }
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

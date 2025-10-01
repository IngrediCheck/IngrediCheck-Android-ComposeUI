package lc.fungee.IngrediCheck.ui.view.screens.feedback
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.WindowInsets

import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import lc.fungee.IngrediCheck.ui.view.component.CameraPreview
import lc.fungee.IngrediCheck.ui.view.component.CameraMode
import lc.fungee.IngrediCheck.ui.view.component.CameraCaptureManager
import io.github.jan.supabase.SupabaseClient
import lc.fungee.IngrediCheck.ui.theme.AppColors

import androidx.lifecycle.viewmodel.compose.viewModel
import lc.fungee.IngrediCheck.IngrediCheckApp

import lc.fungee.IngrediCheck.model.source.image.ImageCache
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import android.widget.Toast
import androidx.compose.ui.draw.clip
import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.model.repository.FeedbackSubmitResult
import lc.fungee.IngrediCheck.viewmodel.FeedbackViewModel
import lc.fungee.IngrediCheck.viewmodel.FeedbackViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    mode: FeedbackMode,
    clientActivityId: String,
    supabaseClient: SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    onRequireReauth: () -> Unit,
    onBack: () -> Unit,
    onOpenCapture: () -> Unit = {},
    externalVm: FeedbackViewModel? = null
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false // allow both partial + expanded
    )
    // declare a focus flag
    var noteFocused by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var showSheet by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }

    val reasons = listOf("Product Images.", "Product Information.", "Incorrect Analysis.")

    val app = IngrediCheckApp.appInstance
    val context = LocalContext.current
    val vm: FeedbackViewModel = externalVm ?: viewModel(
        factory = FeedbackViewModelFactory(
            container = app.container,
            supabaseClient = supabaseClient,
            functionsBaseUrl = functionsBaseUrl,
            anonKey = anonKey,
            appContext = context.applicationContext
        )
    )
    val ui = vm.ui

    // Show sheet as soon as screen loads
    LaunchedEffect(Unit) {
        // Initial stage by mode
        showCamera = when (mode) {
            is FeedbackMode.ImagesOnly -> true
            else -> false
        }
        showSheet = true
        // Open half by default
        scope.launch { sheetState.partialExpand() }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                // Treat dismiss as cancel: delete uploaded images
                scope.launch {
                    vm.deleteUploadedImagesOnCancel()
                    onBack()
                }
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),

            contentWindowInsets = { WindowInsets(0) }
        ) {
            // Force sheet to take half height or full height
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(
                        0.94f
//                        if (sheetState.currentValue == SheetValue.PartiallyExpanded) 0.5f else 1f
                    )
                    .verticalScroll(rememberScrollState()) // ✅ so short content doesn’t shrink the sheet
                    .padding(horizontal = 20.dp,
                        vertical = 1.dp)
                    .windowInsetsPadding(WindowInsets.ime) // ✅ handle keyboard properly
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (showCamera && vm.ui.photos.isNotEmpty()) {
                                    // First clear photos if user is in camera with images
                                    vm.clearImages()
                                }
                                // Then always do back navigation + cleanup
                                vm.deleteUploadedImagesOnCancel()
                                onBack()
                            }
                        }
                    ) {
                        Text("Back", color = AppColors.Brand)
                    }

//                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                        TextButton(onClick = {
//                            scope.launch {
//                                vm.deleteUploadedImagesOnCancel()
//                                onBack()
//                            }
//                        }) { Text("Back", color = PrimaryGreen100) }
//
//                        // Clear visible only in camera stage if there are captured photos
//                        if (showCamera && vm.ui.photos.isNotEmpty()) {
//                            TextButton(onClick = { vm.clearImages() }) { Text("Clear", color = PrimaryGreen100) }
//                        }
//                    }

                    Text("Help me improve 🥺", textAlign = TextAlign.Center)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val canSubmitBase = !ui.isSubmitting && ui.processingCaptures == 0
                        val canSubmit = canSubmitBase && when (mode) {
                            is FeedbackMode.FeedbackOnly -> true
                            is FeedbackMode.ImagesOnly -> ui.photos.isNotEmpty()
                            is FeedbackMode.FeedbackAndImages -> if (showCamera) ui.photos.isNotEmpty() else true
                        }

                        // Top-right action depends on mode and stage
                        if (mode is FeedbackMode.FeedbackAndImages && !showCamera) {
                            TextButton(
                                onClick = {
                                    onOpenCapture()
                                }
                            ) { Text("Next", color = AppColors.Brand) }
                        } else {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        when (val result = vm.submit(clientActivityId)) {
                                            is FeedbackSubmitResult.Success -> {
                                                // Show toast at top of the screen
                                                Toast.makeText(context, "Thanks for your feedback!", Toast.LENGTH_SHORT).apply {
                                                    setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
                                                }.show()
                                                // Clear inputs (reasons, note, photos) after submit
                                                vm.clear()
                                                onBack()
                                            }
                                            is FeedbackSubmitResult.Unauthorized -> {
                                                vm.setError("Authentication required. Please sign in again.")
                                                onRequireReauth()
                                            }
                                            is FeedbackSubmitResult.Failure -> {
                                                vm.setError(result.message ?: "Failed to submit feedback. Please try again.")
                                            }
                                        }
                                    }
                                },
                                enabled = canSubmit
                            ) { Text("Submit", color = AppColors.Brand) }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                if (ui.processingCaptures > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.height(16.dp).width(16.dp), strokeWidth = 2.dp)
                        Text("Processing photos…")
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (showCamera) {
                    // Camera capture view inside the same Feedback sheet (match Check.kt style)
                    CameraPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(435.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        mode = CameraMode.Photo,
                        onPhotoCaptured = { file ->
                            vm.onCapture(file)
                            // Keep camera open for multiple captures (iOS does large detent for capture only)
                        },
                        onBarcodeScanned = { /* no-op in photo mode */ }
                    )

                    // Controls area like Check: center capture button image and left-side latest thumbnail
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        // Center Capture Button (reuses photobutton asset)
                        Image(
                            painter = painterResource(id = R.drawable.photobutton),
                            contentDescription = "Photo Button",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(25))
                                .clickable {
                                    if (CameraCaptureManager.isReady()) {
                                        CameraCaptureManager.takePhoto()
                                    }
                                }
                        )

//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(120.dp) // gives vertical space so CenterStart works
//                        ) {
                            ui.photos.lastOrNull()?.let { info ->
                                val previewSource = if (info.imageFileHash != null) {
                                    ImageCache.fileFor(context, info.imageFileHash, ImageCache.Size.SMALL)
                                } else info.localFile

                                previewSource?.let { src ->
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart) // left side, vertically centered
                                            .width(80.dp)
                                            .height(100.dp)
                                    ) {
                                        // Image preview
                                        Image(
                                            painter = rememberAsyncImagePainter(src),
                                            contentDescription = "Last Captured Photo",
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clip(RoundedCornerShape(15.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        // ❌ delete button overlay (top-right corner of the image)
                                        IconButton(
                                            onClick = {
                                                val hash = info.imageFileHash
                                                if (hash != null) vm.removeImage(hash)
                                                else vm.removeLocalPhoto(info.tempId)
                                            },
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove Photo",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
//                            }
                        }

//                        // Left-side latest Captured Image Preview (small)
//                        ui.photos.lastOrNull()?.let { info ->
//                            val previewSource = if (info.imageFileHash != null) {
//                                ImageCache.fileFor(context, info.imageFileHash, ImageCache.Size.SMALL)
//                            } else info.localFile
//                            previewSource?.let { src ->
//                                androidx.compose.foundation.Image(
//                                    painter = rememberAsyncImagePainter(src),
//                                    contentDescription = "Last Captured Photo",
//                                    modifier = Modifier
//                                        .align(Alignment.CenterStart)
//                                        .width(80.dp)
//                                        .height(100.dp)
//                                        .clip(RoundedCornerShape(15.dp)),
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
//                        }
                    }

//                    Spacer(Modifier.height(12.dp))
//                    if (ui.photos.isNotEmpty()) {
////                        Text("Attached photos: ${'$'}{ui.photos.size}")
//                        Spacer(Modifier.height(8.dp))
//                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                            items(ui.photos, key = { it.tempId }) { photo ->
//                                val source = if (photo.imageFileHash != null) {
//                                    // remember by tempId to avoid nullable key
//                                    remember(photo.tempId) { ImageCache.fileFor(context, photo.imageFileHash, ImageCache.Size.SMALL) }
//                                } else photo.localFile
//                                Box(
//                                    modifier = Modifier
//                                        .width(90.dp)
//                                        .height(90.dp)
//                                ) {
//                                    source?.let { src ->
//                                        androidx.compose.foundation.Image(
//                                            painter = rememberAsyncImagePainter(src),
//                                            contentDescription = "attachment",
//                                            modifier = Modifier
//                                                .matchParentSize()
//                                                .clip(RoundedCornerShape(8.dp)),
//                                            contentScale = ContentScale.Crop
//                                        )
//                                    }
//                                    IconButton(
//                                        onClick = {
//                                            val hash = photo.imageFileHash
//                                            if (hash != null) vm.removeImage(hash) else vm.removeLocalPhoto(photo.tempId)
//                                        },
//                                        modifier = Modifier.align(Alignment.TopEnd)
//                                    ) {
//                                        Icon(Icons.Default.Close, contentDescription = "Remove")
//                                    }
//                                }
//                            }
//                        }
//                    }
                } else {
                    // Feedback form view
                    // Question
                    Text(
                        "What should I look into?",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        reasons.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        vm.toggleReason(option)
                                    }
                            ) {
                                Checkbox(
                                    checked = ui.reasons.contains(option),
                                    onCheckedChange = { vm.toggleReason(option) }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = option, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ui.note,
                        onValueChange = { vm.setNote(it) },
                        placeholder = { Text("Optionally, leave me a note here.") },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = false,
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    // Expand the sheet to avoid keyboard overlap (public API)
                                    scope.launch { sheetState.expand() }
                                }
                            }
                    )
                    
//                                val source = if (photo.imageFileHash != null) {
//                                    remember(photo.tempId) { ImageCache.fileFor(context, photo.imageFileHash, ImageCache.Size.SMALL) }
//                                } else photo.localFile
//                                Box(
//                                    modifier = Modifier
//                                        .width(90.dp)
//                                        .height(90.dp)
//                                ) {
//                                    source?.let { src ->
//                                        androidx.compose.foundation.Image(
//                                            painter = rememberAsyncImagePainter(src),
//                                            contentDescription = "attachment",
//                                            modifier = Modifier
//                                                .matchParentSize()
//                                                .clip(RoundedCornerShape(8.dp)),
//                                            contentScale = ContentScale.Crop
//                                        )
//                                    }
//                                    IconButton(
//                                        onClick = {
//                                            val hash = photo.imageFileHash
//                                            if (hash != null) vm.removeImage(hash) else vm.removeLocalPhoto(photo.tempId)
//                                        },
//                                        modifier = Modifier.align(Alignment.TopEnd)
//                                    ) {
//                                        Icon(Icons.Default.Close, contentDescription = "Remove")
//                                    }
                                }
                            }
                        }
                    }

                    ui.error?.let { err ->
                        Spacer(Modifier.height(8.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.error)
                    }
                }
//            }
//        }
//    }
//}

package lc.fungee.IngrediCheck.ui.view.screens.feedback

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import lc.fungee.IngrediCheck.ui.view.component.CameraCaptureManager
import lc.fungee.IngrediCheck.ui.view.component.CameraMode
import lc.fungee.IngrediCheck.ui.view.component.CameraPreview
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.model.source.image.ImageCache
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.SheetValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.model.repository.FeedbackSubmitResult
import lc.fungee.IngrediCheck.viewmodel.FeedbackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackCaptureSheet(
    vm: FeedbackViewModel,
    onDismiss: () -> Unit,
    clientActivityId: String,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    var show by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val ui = vm.ui

    LaunchedEffect(Unit) {
        show = true
        // open half by default
        sheetState.partialExpand()
    }

    if (!show) return

    ModalBottomSheet(
        onDismissRequest = { show = false; onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        contentWindowInsets = { WindowInsets(0) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .windowInsetsPadding(WindowInsets.ime)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    scope.launch {
                        if (vm.ui.photos.isNotEmpty()) {
                            // Clear photos then go back
                            vm.clearImages()
                            Toast.makeText(context, "Feedback cleared", Toast.LENGTH_SHORT).show()
                        } else {
                            // Cancel: delete uploads (if any) and go back
                            vm.deleteUploadedImagesOnCancel()
                        }
                        show = false
                        onDismiss()
                    }
                }) { Text("Back", color = AppColors.Brand) }

                Text("Add Photos", style = MaterialTheme.typography.titleMedium)

            // Submit on the right, same style/color as Back
            val canSubmitBase = !vm.ui.isSubmitting && vm.ui.processingCaptures == 0 && vm.hasSession()
            val hasUploadedPhoto = vm.ui.photos.any { it.imageFileHash != null }
            val canSubmit = canSubmitBase && hasUploadedPhoto

            TextButton(
                onClick = {
                    scope.launch {
                        val result = vm.submit(clientActivityId)
                        when (result) {
                            is FeedbackSubmitResult.Success -> {
                                Toast.makeText(context, "Feedback submitted!", Toast.LENGTH_SHORT).show()
                                show = false
                                onDismiss()
                            }
                            is FeedbackSubmitResult.Unauthorized -> {
                                Toast.makeText(context, "Unauthorized. Please log in again.", Toast.LENGTH_SHORT).show()
                            }
                            is FeedbackSubmitResult.Failure -> {
                                Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                },
                enabled = canSubmit
            ) {
                if (vm.ui.isSubmitting) {
                    CircularProgressIndicator(
                        color = AppColors.Brand,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text("Submit", color = AppColors.Brand)
                }
            }

            }
            // Submit moved to header row as TextButton; removed large button
            // Dynamic camera height based on sheet state
            val targetHeight: Dp = when (sheetState.currentValue) {
                SheetValue.PartiallyExpanded -> 320.dp
                SheetValue.Expanded -> 435.dp
                else -> 320.dp
            }
            val cameraHeight by animateDpAsState(targetValue = targetHeight, label = "cameraHeight")

            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(cameraHeight)
                    .clip(RoundedCornerShape(8.dp)),
                mode = CameraMode.Photo,
                onPhotoCaptured = { file ->
                    vm.onCapture(file)
                },
                onBarcodeScanned = { /* no-op */ }
            )

            // Controls row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Center capture button
                Image(
                    painter = painterResource(id = R.drawable.photobutton),
                    contentDescription = "Capture",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(25))
                        .clickable {
                            if (CameraCaptureManager.isReady()) {
                                CameraCaptureManager.takePhoto()
                            }
                        }
                )

                // Left-side last thumbnail
                ui.photos.lastOrNull()?.let { photo ->
                    val src = photo.imageFileHash?.let { ImageCache.fileFor(context, it, ImageCache.Size.SMALL) }
                        ?: photo.localFile
                    src?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = "Last photo",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(80.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(15.dp))
                        )
                    }
                }
            }
        }
    }
}


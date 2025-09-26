package lc.fungee.IngrediCheck.ui.screens.feedback

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
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
import lc.fungee.IngrediCheck.ui.component.CameraCaptureManager
import lc.fungee.IngrediCheck.ui.component.CameraMode
import lc.fungee.IngrediCheck.ui.component.CameraPreview
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.data.source.image.ImageCache
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.SheetValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackCaptureSheet(
    vm: FeedbackViewModel,
    onDismiss: () -> Unit,
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

                // Right side kept empty to avoid duplicate buttons
                Spacer(Modifier.width(64.dp))
            }

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
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = lc.fungee.IngrediCheck.R.drawable.photobutton),
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
                        androidx.compose.foundation.Image(
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

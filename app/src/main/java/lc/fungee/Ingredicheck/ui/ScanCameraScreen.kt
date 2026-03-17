package lc.fungee.Ingredicheck.ui
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.model.ScannerViewModel
import lc.fungee.Ingredicheck.model.ScanResponse
import lc.fungee.Ingredicheck.ui.components.IOSStyleLoadingSpinner
import lc.fungee.Ingredicheck.ui.components.buttons.ScannerSwipeButton
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito

/**
 * Scanner screen that provides real camera access using CameraX.
 */
@Composable
fun ScanCameraScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val viewModel: ScannerViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    val hasCameraPermission = uiState.hasCameraPermission
    val showPermissionRationale = uiState.showPermissionRationale
    val isTorchOn = uiState.isTorchOn

    var camera by remember { mutableStateOf<Camera?>(null) }
    var scanAreaBottomPx by remember { mutableStateOf<Float?>(null) }
    var lastStreamedBarcode by remember { mutableStateOf<String?>(null) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        val initialGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.onInitialPermissionCheck(initialGranted)

        if (!initialGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (hasCameraPermission) {
            // Real Camera Preview using CameraX
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onCameraReady = { boundCamera ->
                    camera = boundCamera
                },
                onBarcodeDetected = { barcode ->
                    // Debounce/Filter: Only process if it's a new code AND we aren't currently "Locked" 
                    // onto a scanning session that is less than 2 seconds old (unless it's the exact same code)
                    val currentScan = viewModel.scanResult.value
                    val isBusy = currentScan != null && (currentScan.state == "fetching_product_info" || currentScan.state == "analyzing")
                    
                    if (barcode != lastStreamedBarcode) {
                        if (isBusy) {
                            // If we are currently busy with a different barcode, ignore the new one for a bit
                            // to avoid the "Cancellation Storm" seen in logs
                             android.util.Log.d("ScanCameraScreen", "Busy with ${currentScan?.barcode}; ignoring new barcode $barcode")
                        } else {
                            android.util.Log.d(
                                "ScanCameraScreen",
                                "New barcode detected: $barcode (last=$lastStreamedBarcode)"
                            )
                            lastStreamedBarcode = barcode
                            // Haptic feedback on new barcode
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch {
                                val token = authViewModel.accessTokenOrNull()
                                if (!token.isNullOrBlank()) {
                                    viewModel.startBarcodeStream(barcode, token)
                                }
                            }
                        }
                    }
                }
            )
        } else {
            // Permission Denied / Rationale UI
            PermissionDeniedContent(
                showRationale = showPermissionRationale,
                onOpenSettings = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                },
                onBack = onClose
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)          // distance from top of screen
                .padding(horizontal = 20.dp)
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Overlay: Back button and Torch (drawn above dim overlay)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33E8E8E8))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_chevron_back),
                        contentDescription = "Close",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33E8E8E8))
                        .clickable {
                            val newState = !isTorchOn
                            viewModel.setTorchEnabled(newState)
                            camera?.cameraControl?.enableTorch(newState)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isTorchOn) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                        contentDescription = if (isTorchOn) "Torch on" else "Torch off",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }


            }
            Spacer(modifier = Modifier.height(24.dp))

            // Guidance capsule + 66dp spacer, measured to drive scan window position
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        // bottom of guidance + 66dp spacer
                        scanAreaBottomPx = coords.boundsInRoot().bottom
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
//                            .fillMaxWidth()
                            .background(
                                color = Color(0x33E8E8E8),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                                Image(
                                    painter = painterResource(id = R.drawable.light_and_stars),
                                    contentDescription = "lights and stars",
                                    colorFilter = ColorFilter.tint(Color.White),
                                    modifier = Modifier.size(20.dp)
                                )

                                Text(
                                    text = " Ensure good lighting and steady hands",
                                    color = Color.White,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }


                   Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }

        // Full-screen dim overlay + transparent cutout + scanner frame & scan line
        if (hasCameraPermission) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 1f)
            ) {
                val density = LocalDensity.current
                val frameWidth = maxWidth * 0.8f
                val aspectRatio = 286f / 121f
                val frameHeight = frameWidth / aspectRatio

                // Position frame using measured bottom of guidance+66dp spacer from column
                val frameTopOffset = scanAreaBottomPx?.let { with(density) { it.toDp() } } ?: 0.dp
                val left = (maxWidth - frameWidth) / 2

                // Animated scan line inside the frame
                val frameHeightPx = with(density) { frameHeight.toPx() - 4.dp.toPx() }
                val infiniteTransition = rememberInfiniteTransition(label = "scanLineTransition")
                val scanLineOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = frameHeightPx.coerceAtLeast(0f),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1800,
                            easing = FastOutSlowInEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scanLineOffset"
                )

                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer(alpha = 1f)
                ) {
                    // Dim the entire screen
                    drawRect(color = Color.Black.copy(alpha = 0.6f))

                    // Clear the rounded rectangle scan window
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = Offset(
                            left.toPx(),
                            with(density) { frameTopOffset.toPx() }
                        ),
                        size = Size(frameWidth.toPx(), frameHeight.toPx()),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                        blendMode = BlendMode.Clear
                    )
                }

                // Vector frame and animated scan line on top of the transparent cutout
                Box(
                    modifier = Modifier
                        .size(width = frameWidth, height = frameHeight)
                        .align(Alignment.TopCenter)
                        .offset(y = frameTopOffset)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    val scanLineColor = Color(0xFFFDB518)
                    val scanLineY = with(density) { scanLineOffset.toDp() }
                    val maxTail = (frameHeight - scanLineY).coerceAtLeast(0.dp)
                    val tailHeight = (maxTail * 0.35f).coerceAtLeast(0.dp)

                    if (tailHeight > 0.dp) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(tailHeight)
                                .offset(y = scanLineY)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x8AFDB518),
                                            Color(0x00FDB518)
                                        )
                                    )
                                )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .offset(y = scanLineY)
                            .background(scanLineColor)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.scanner_container),
                        contentDescription = "Scanner frame",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                // Helper text and empty product card below the scan window
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = frameTopOffset + frameHeight + 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Align the barcode within the\nframe to scan",
                        color = Color.White,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(140.dp))

                    // Simple debug/product info from SSE so you can test quickly
                    ProductDetailCardinCameraScreen(
                        scanResult = scanResult
                    )

//                    Text(
//                        text = productName,
//                        color = Color.White,
//                        fontFamily = Manrope,
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 16.sp,
//                        textAlign = TextAlign.Center
//                    )
//
//                    if (!brand.isNullOrBlank()) {
//                        Text(
//                            text = brand,
//                            color = Color.White.copy(alpha = 0.8f),
//                            fontFamily = Manrope,
//                            fontWeight = FontWeight.Normal,
//                            fontSize = 14.sp,
//                            textAlign = TextAlign.Center
//                        )
//                    }
//
//                    Text(
//                        text = "State: $stateLabel",
//                        color = Color.White.copy(alpha = 0.7f),
//                        fontFamily = Manrope,
//                        fontWeight = FontWeight.Normal,
//                        fontSize = 12.sp,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )

                }
            }
        }

        // Bottom scanner swipe button, above system navigation bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = navBarBottomPadding ),
            contentAlignment = Alignment.Center
        ) {
                    ScannerSwipeButton()
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onCameraReady: (Camera) -> Unit = {},
    onBarcodeDetected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(
                                ContextCompat.getMainExecutor(ctx),
                                BarcodeAnalyzer { barcode ->
                                    onBarcodeDetected(barcode)
                                }
                            )
                        }

                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    onCameraReady(camera)
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

@Composable
private fun PermissionDeniedContent(
    showRationale: Boolean,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.tab_bar_scanner_icon),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.White.copy(alpha = 0.3f)
        )
        
        Text(
            text = "Camera Access Required",
            color = Color.White,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
        
        Text(
            text = if (showRationale) 
                "To scan product barcodes and get instant analysis, we need access to your camera. Please enable it in settings." 
                else "Allow camera access to start scanning products for food notes analysis.",
            color = Color.White.copy(alpha = 0.7f),
            fontFamily = Nunito,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 48.dp)
        )

        Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF91B640)), // Brand Green
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Enable Camera Access",
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Text(
            text = "Not now",
            color = Color.White.copy(alpha = 0.5f),
            fontFamily = Nunito,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(top = 24.dp)
                .clickable { onBack() }
        )
    }
}

@Composable
fun ProductDetailCardinCameraScreen(
    scanResult: ScanResponse?,
    modifier: Modifier = Modifier
) {
    // Idle / skeleton state when there is no scan yet
    if (scanResult == null) {
        Row(
            modifier = modifier
                .size(width = 300.dp, height = 120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x33E8E8E8))
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(width = 68.dp, height = 92.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x33E8E8E8))
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .size(width = 185.dp, height = 25.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x33E8E8E8))
                ) {}
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .size(width = 132.dp, height = 20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x33E8E8E8))
                ) {}
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .size(width = 79.dp, height = 24.dp)
                        .clip(RoundedCornerShape(52.dp))
                        .background(Color(0x33E8E8E8))
                ) {}
            }
        }
        return
    }

    // Active scan state (pending / analyzing / done)
    val productName = scanResult.productInfo?.name
    val brandName = scanResult.productInfo?.brand
    val hasProductInfo = !productName.isNullOrBlank() || !brandName.isNullOrBlank()

    // Try to use first image URL when available
    val imageUrl = scanResult.images.firstOrNull()?.url
    val showRealImage = !imageUrl.isNullOrBlank()

    Row(
        modifier = modifier
            .size(width = 300.dp, height = 120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x33E8E8E8))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left side: product image or placeholder barcode frame
        Box(
            modifier = Modifier
                .size(width = 71.dp, height = 95.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (showRealImage) {
                coil.compose.SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "Product image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.barcode_scaning_frame),
                    contentDescription = "Barcode placeholder",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
        }

        // Right side: text + loading capsule or brand/product
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (!hasProductInfo) {
                // Pending state: looking up product
                Text(
                    text = "Looking up this product…",
                    color = Color.White,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "We’re checking our database for this product.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(15.dp))

                // Gradient capsule with spinner and "Fetching details"
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFFEAEAEA)
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFFA6A6A6),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Fetching details",
                        color = Color(0xFF4C4C4C),
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            } else {
                // Product found: show brand and product name
                if (!brandName.isNullOrBlank()) {
                    Text(
                        text = brandName,
                        color = Color.White,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (!productName.isNullOrBlank()) {
                    Text(
                        text = productName,
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun EmptyProductDetailCardPreview() {
//    EmptyProductDetailCard()
//}


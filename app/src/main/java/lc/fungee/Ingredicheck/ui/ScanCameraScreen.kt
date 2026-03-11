package lc.fungee.Ingredicheck.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import lc.fungee.Ingredicheck.R
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
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            showPermissionRationale = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (hasCameraPermission) {
            // Real Camera Preview using CameraX
            CameraPreview(
                modifier = Modifier.fillMaxSize()
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

        // Top Overlay: Back button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .padding(horizontal = 20.dp),
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

            



        }

        // Scanner Frame Overlay (Visual only for now)
        if (hasCameraPermission) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Dim overlay with transparent cutout in the center
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 1f)
                ) {
                    val density = LocalDensity.current
                    val frameWidth = maxWidth * 0.8f
                    val aspectRatio = 286f / 121f
                    val frameHeight = frameWidth / aspectRatio

                    val left = (maxWidth - frameWidth) / 2
                    val top = (maxHeight - frameHeight) / 2

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

                    val scanLineColor = Color(0xFFFDB518)

                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer(alpha = 1f)
                    ) {
                        // Draw full-screen dim layer
                        drawRect(color = Color.Black.copy(alpha = 0.6f))

                        // Clear the center rounded rectangle to reveal the camera preview
                        drawRoundRect(
                            color = Color.Transparent,
                            topLeft = Offset(left.toPx(), top.toPx()),
                            size = Size(frameWidth.toPx(), frameHeight.toPx()),
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                            blendMode = BlendMode.Clear
                        )
                    }

                    // Vector frame and animated scan line on top of the transparent cutout
                    Box(
                        modifier = Modifier
                            .size(width = frameWidth, height = frameHeight)
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        // Moving gradient scan line inside the frame
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .offset(y = with(density) { scanLineOffset.toDp() })
                                .background(scanLineColor)
                        )

                        // Scanner frame vector above the scan line
                        Image(
                            painter = painterResource(id = R.drawable.scanner_container),
                            contentDescription = "Scanner frame",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                    }

                    Text(
                        text = "Center the barcode within the frame",
                        color = Color.White,
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 200.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
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
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
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


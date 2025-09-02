package lc.fungee.IngrediCheck.ui.screens.check

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import lc.fungee.IngrediCheck.data.source.hapticSuccess
import lc.fungee.IngrediCheck.ui.theme.White
import kotlinx.coroutines.delay
import java.io.File

// Controls whether the camera is scanning barcodes or capturing photos
enum class CameraMode { Scan, Photo }

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    mode: CameraMode,
    onPhotoCaptured: (File) -> Unit,
    onBarcodeScanned: (String?) -> Unit
) {
    var barcodeDetected by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var didVibrate by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Track permission state
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher for camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request permission when composable is first launched
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Show hint once after 4s if scan hasn't happened (only in Scan mode)
    LaunchedEffect(barcodeDetected, mode) {
        if (mode == CameraMode.Scan && !barcodeDetected) {
            delay(4000)
            if (!barcodeDetected) {
                showMessage = true
                Toast.makeText(context, "Please scan the code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Reset scan vibration flag when switching back to Scan
    LaunchedEffect(mode) {
        if (mode == CameraMode.Scan) {
            didVibrate = false
            barcodeDetected = false
        }
    }

    // Use cases shared across modes
    val options = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_8, Barcode.FORMAT_EAN_13)
            .build()
    }
    val scanner = remember { BarcodeScanning.getClient(options) }

    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }
    // Set analyzer separately so we can keep the instance but only bind in Scan mode
    LaunchedEffect(Unit) {
        imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            // Only process if currently in Scan mode
            if (mode != CameraMode.Scan) {
                imageProxy.close()
                return@setAnalyzer
            }
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            val rawValue = barcode.rawValue
                            Log.d("Barcode", "Scanned: $rawValue")
                            if (!rawValue.isNullOrEmpty() && !didVibrate) {
                                didVibrate = true
                                barcodeDetected = true
                                hapticSuccess(context)
                                onBarcodeScanned(rawValue)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Barcode", "Error: ", e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // Camera Preview with dynamic binding per mode
    val selector = remember { CameraSelector.DEFAULT_BACK_CAMERA }
    val cameraRef = remember { mutableStateOf<Camera?>(null) }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
        },
        modifier = modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProvider = ProcessCameraProvider.getInstance(previewView.context).get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                val useCases = mutableListOf<UseCase>(preview)
                if (mode == CameraMode.Scan) useCases.add(imageAnalyzer)
                if (mode == CameraMode.Photo) useCases.add(imageCapture)

                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    *useCases.toTypedArray()
                )
                cameraRef.value = camera

                // Pinch-to-zoom
                val scaleGestureDetector = android.view.ScaleGestureDetector(
                    previewView.context,
                    object : android.view.ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(detector: android.view.ScaleGestureDetector): Boolean {
                            val currentZoom = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                            val delta = detector.scaleFactor
                            camera.cameraControl.setZoomRatio(currentZoom * delta)
                            return true
                        }
                    }
                )
                previewView.setOnTouchListener { _, event ->
                    scaleGestureDetector.onTouchEvent(event)
                    true
                }

                // Store ImageCapture globally for Photo mode
                CameraCaptureManager.init(imageCapture, context, onPhotoCaptured)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }
    )

    if (!hasPermission) {
        // PERMISSION PLACEHOLDER
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        )
    }
}

object CameraCaptureManager {
    private var imageCapture: ImageCapture? = null
    private var context: Context? = null
    private var callback: ((File) -> Unit)? = null

    fun init(imageCapture: ImageCapture, ctx: Context, cb: (File) -> Unit) {
        this.imageCapture = imageCapture
        this.context = ctx
        this.callback = cb
    }

    fun takePhoto() {
        val capture = imageCapture ?: return
        val photoFile = File(
            context?.cacheDir,
            "captured-${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context!!),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    callback?.invoke(photoFile) // send file back
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }
}

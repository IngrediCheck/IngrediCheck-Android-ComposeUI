package lc.fungee.IngrediCheck.ui.screens.check

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
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100
import lc.fungee.IngrediCheck.ui.theme.White
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import lc.fungee.IngrediCheck.data.model.CheckSheetState
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.coroutineScope
import java.io.File
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import android.widget.Toast
import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import lc.fungee.IngrediCheck.data.model.ImageInfo
import io.github.jan.supabase.storage.storage
import java.security.MessageDigest
import io.ktor.http.ContentType

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
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
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

                var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.94f)
                        .padding(start = 22.dp, end = 22.dp, top = 30.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .width(230.dp)
                            .height(30.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = selectedItemIndex,
                            containerColor = androidx.compose.ui.graphics.Color.LightGray,
                            modifier = Modifier.clip(RoundedCornerShape(20)),

                            indicator = { tabPositions ->
                                if (selectedItemIndex < tabPositions.size) {
                                    Box(
                                        Modifier
                                            .tabIndicatorOffset(tabPositions[selectedItemIndex])
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(30))
                                            .background(PrimaryGreen100)
                                            .zIndex(1f)
                                    )
                                }
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedItemIndex == index,
                                    onClick = { selectedItemIndex = index },
                                    text = { Text(title, color = LabelsPrimary) },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .zIndex(2f)
                                )
                            }
                        }
                    }

                    // Camera
                    var capturedImage by remember { mutableStateOf<File?>(null) }
                    var scannedCode by remember { mutableStateOf<String?>(null) }

                    CameraPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
                            .height(435.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        mode = if (selectedItemIndex == 0) CameraMode.Scan else CameraMode.Photo,
                        onPhotoCaptured = { file ->
                            capturedImage = file
                            // Run OCR, still-image barcode, and upload in parallel; then navigate to image-based Analysis
                            scope.launch {
                                val ocrDeferred = async { runTextRecognition(file, context) }
                                val barcodeDeferred = async { detectBarcodeFromImage(file, context) }
                                val uploadDeferred = async { uploadImageToSupabase(file, supabaseClient) }

                                val ocrText = runCatching { ocrDeferred.await() }.getOrElse { "" }
                                val barcode = runCatching { barcodeDeferred.await() }.getOrNull()
                                val imageHash = runCatching { uploadDeferred.await() }.getOrElse { err ->
                                    Log.e("Upload", "Image upload failed", err)
                                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                    null
                                }

                                if (imageHash != null) {
                                    val imageInfo = ImageInfo(
                                        imageFileHash = imageHash,
                                        imageOCRText = ocrText,
                                        barcode = barcode
                                    )
                                    sheetContent = CheckSheetState.Analysis(images = listOf(imageInfo))
                                }
                            }
                        }, onBarcodeScanned = { value ->
                            scannedCode = value
                            if (!value.isNullOrEmpty()) {
                                // Switch to Analysis inside the same bottom sheet
                                sheetContent = CheckSheetState.Analysis(barcode = value)
                            }
                        }
                    )

                    Text(
                        "${texts[selectedItemIndex]}", color = LabelsPrimary, modifier = Modifier
                            .padding(top = 20.dp)
                    )

                    if (selectedItemIndex == 1) {
                        Image(
                            painter = painterResource(id = lc.fungee.IngrediCheck.R.drawable.photobutton),
                            contentDescription = "Photo Button",
                            modifier = Modifier
                                .clip(RoundedCornerShape(25))
                                .clickable {
                                    if (CameraCaptureManager.isReady()) {
                                        CameraCaptureManager.takePhoto()
                                    } else {
                                        Toast.makeText(context, "Camera not ready yet", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        )


                    }

                    capturedImage?.let { file ->
                        Image(
                            painter = rememberAsyncImagePainter(file),
                            contentDescription = "Captured Photo",
                            modifier = Modifier.width(80.dp).height(100.dp)
                                .clip(RoundedCornerShape(15.dp)).align(alignment = Alignment.Start),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            is CheckSheetState.Analysis -> {
                AnalysisScreen(
                    barcode = state.barcode,
                    images = state.images,
                    supabaseClient = supabaseClient,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey
                )
            }
        }
    }
}

// MLKit OCR on a captured file
suspend fun runTextRecognition(file: File, context: Context): String {
    return try {
        val image = InputImage.fromFilePath(context, Uri.fromFile(file))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image).await()
        result.text
    } catch (e: Exception) {
        Log.e("OCR", "Error during text recognition", e)
        ""
    }
}

// MLKit barcode detection on a captured file
suspend fun detectBarcodeFromImage(file: File, context: Context): String? {
    return try {
        val image = InputImage.fromFilePath(context, Uri.fromFile(file))
        val scanner = BarcodeScanning.getClient()
        val barcodes = scanner.process(image).await()
        barcodes.firstOrNull { it.rawValue?.isNotBlank() == true &&
            (it.format == Barcode.FORMAT_EAN_8 || it.format == Barcode.FORMAT_EAN_13)
        }?.rawValue
    } catch (e: Exception) {
        Log.e("Barcode", "Error detecting barcode from image", e)
        null
    }
}

// Supabase image upload using SHA-256 filename into bucket "productimages"
suspend fun uploadImageToSupabase(file: File, supabaseClient: SupabaseClient): String {
    val bytes = file.readBytes()
    val hash = sha256Hex(bytes)
    return try {
        // Use upsert=true to be idempotent across retries
        supabaseClient.storage.from("productimages").upload(
            path = hash,
            data = bytes
        ) {
            upsert = true
            contentType = ContentType.Image.JPEG
        }
        hash
    } catch (e: Exception) {
        // If already exists or minor errors, still proceed if server can access by hash
        Log.w("Upload", "Upload error, proceeding with hash: ${'$'}hash", e)
        hash
    }
}

private fun sha256Hex(data: ByteArray): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(data)
    val sb = StringBuilder()
    for (b in digest) {
        sb.append(String.format("%02x", b))
    }
    return sb.toString()
}

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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import lc.fungee.IngrediCheck.data.model.ImageInfo
import io.github.jan.supabase.storage.storage
import java.security.MessageDigest
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository

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
                                // Get an access token to pass RLS for Storage upload
                                val prefRepo = PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey)
                                val accessToken = runCatching { prefRepo.currentToken() }.getOrNull()
                                val uploadDeferred = async {
                                    uploadImageToSupabase(
                                        file = file,
                                        supabaseClient = supabaseClient,
                                        functionsBaseUrl = functionsBaseUrl,
                                        anonKey = anonKey,
                                        accessToken = accessToken
                                    )
                                }

                                val ocrText = runCatching { ocrDeferred.await() }.getOrElse { "" }
                                val barcode = runCatching { barcodeDeferred.await() }.getOrNull()
                                val imageHash = runCatching { uploadDeferred.await() }.getOrNull()

                                if (imageHash != null) {
                                    val imageInfo = ImageInfo(
                                        imageFileHash = imageHash,
                                        imageOCRText = ocrText,
                                        barcode = barcode
                                    )
                                    sheetContent = CheckSheetState.Analysis(images = listOf(imageInfo))
                                } else {
                                    Toast.makeText(context, "Image upload failed. Please sign in and try again.", Toast.LENGTH_LONG).show()
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
suspend fun uploadImageToSupabase(
    file: File,
    supabaseClient: SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    accessToken: String?
): String? {
    // Hash original JPEG bytes for deterministic filename (same as iOS)
    val bytes = withContext(Dispatchers.IO) { file.readBytes() }
    val hash = sha256Hex(bytes)

    // Require an access token to satisfy RLS when writing to Storage
    if (accessToken.isNullOrBlank()) {
        Log.e("Upload", "No access token; cannot upload to Storage due to RLS")
        return null
    }

    val baseUrl = functionsBaseUrl.substringBefore("/functions/")
    val url = "$baseUrl/storage/v1/object/productimages/$hash"
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        try {
            val mediaType = "image/jpeg".toMediaTypeOrNull() ?: run {
                Log.e("Upload", "Invalid media type")
                return@withContext null
            }
            
            val requestBody = RequestBody.create(mediaType, bytes)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("apikey", anonKey)
                .addHeader("x-upsert", "true")
                .build()
                
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    Log.e("Upload", "Storage upload failed: code=${'$'}{response.code}, body=${'$'}{responseBody?.take(200)}")
                    return@withContext null
                }
                Log.d("Upload", "Upload successful, hash: $hash")
                hash
            }
        } catch (e: Exception) {
            Log.e("Upload", "Exception during upload", e)
            null
        } finally {
            try {
                client.dispatcher.executorService.shutdown()
            } catch (e: Exception) {
                Log.e("Upload", "Error shutting down client", e)
            }
        }
    }
}

// No JWT parsing needed in this file

private fun compressJpeg(file: File, maxDimension: Int, quality: Int): ByteArray {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, options)
    val (w, h) = options.outWidth to options.outHeight
    var inSampleSize = 1
    if (w > maxDimension || h > maxDimension) {
        val halfW = w / 2
        val halfH = h / 2
        while ((halfW / inSampleSize) >= maxDimension || (halfH / inSampleSize) >= maxDimension) {
            inSampleSize *= 2
        }
    }
    val decodeOpts = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
    val bmp = BitmapFactory.decodeFile(file.absolutePath, decodeOpts)
        ?: return file.readBytes()
    val bos = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(50, 95), bos)
    return bos.toByteArray()
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

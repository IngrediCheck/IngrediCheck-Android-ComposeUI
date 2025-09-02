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
import java.io.File


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
                        }, onBarcodeScanned = { value ->
                            scannedCode = value
                            if (!value.isNullOrEmpty()) {
                                // Switch to Analysis inside the same bottom sheet
                                sheetContent = CheckSheetState.Analysis(value)
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
                                .clickable { CameraCaptureManager.takePhoto() }
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
                    supabaseClient = supabaseClient,
                    functionsBaseUrl = functionsBaseUrl,
                    anonKey = anonKey
                )
            }
        }
    }
}

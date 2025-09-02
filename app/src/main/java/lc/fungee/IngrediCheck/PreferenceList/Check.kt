//// Updated: app/src/main/java/lc/fungee/IngrediCheck/PreferenceList/Check.kt
package lc.fungee.IngrediCheck.PreferenceList
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.analysisresult.MyBottomSheetExample
import lc.fungee.IngrediCheck.ui.theme.White

@Composable
fun CameraScreen(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Button(onClick = { showBottomSheet = true }) {
        Text("hi")
    }

    if (showBottomSheet) {
        MyBottomSheetExample(
            onDismiss = { showBottomSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheetExample(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Button(onClick = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
        }) {
            Text("Hide bottom sheet")
        }
    }
}
//
//import android.Manifest
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import lc.fungee.IngrediCheck.ui.theme.White
//
//@Composable
//fun CameraScreen(navController: NavController) {
//
//
////    Scaffold(
////        bottomBar = {
////            BottomBar(navController)
////        }
////    ) { paddingValues ->
////        Box(
////            modifier = Modifier
////                .fillMaxSize()
////                .padding(paddingValues).background(color = White)
////                .padding(24.dp),
////            contentAlignment = Alignment.Center
////        ) {
////            Text(
////                text = "Camera Screen",
////                fontSize = 22.sp,
////                fontWeight = FontWeight.Bold
////            )
////        }
////    }
//
//}
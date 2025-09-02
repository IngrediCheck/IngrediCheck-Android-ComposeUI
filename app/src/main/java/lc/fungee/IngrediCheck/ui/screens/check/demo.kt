//package lc.fungee.IngrediCheck.ui.screens.check
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import io.ktor.websocket.Frame
//import lc.fungee.IngrediCheck.data.model.SafetyRecommendation
//import lc.fungee.IngrediCheck.data.model.decoratedIngredientsList
//import lc.fungee.IngrediCheck.data.repository.AnalysisViewModel
//import java.util.UUID
//@Composable
//fun AnalysisScreen(
//    barcode: String,
//    viewModel: AnalysisViewModel = remember { AnalysisViewModel() } // no Hilt
//){
//    val product = viewModel.product
//    val recs = viewModel.recommendations
//    val error = viewModel.error
//
//    LaunchedEffect(barcode) {
//        viewModel.analyzeBarcode(UUID.randomUUID().toString(), barcode)
//    }
//
//    when {
//        error != null -> Frame.Text("Error: $error")
//        product == null -> CircularProgressIndicator()
//        else -> {
//            Column {
//                Text(product.name, fontWeight = FontWeight.Bold)
//                Text(product.brand?:"unknown")
//
//                val ingredientsList = product.ingredients
//                    ?.split(",")
//                    ?.map { it.trim() }
//                    ?: emptyList()
//
//                val decorated = decoratedIngredientsList(ingredientsList, recs)
//                decorated.forEach {
//                    Text(
//                        it.fragment,
//                        color = when (it.safetyRecommendation) {
//                            SafetyRecommendation.MaybeUnsafe -> Color.Yellow
//                            SafetyRecommendation.DefinitelyUnsafe -> Color.Red
//                            else -> Color.Black
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

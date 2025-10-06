

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import lc.fungee.IngrediCheck.model.entities.DecoratedIngredientListFragment
import lc.fungee.IngrediCheck.model.entities.IngredientRecommendation
import lc.fungee.IngrediCheck.model.entities.Product
import lc.fungee.IngrediCheck.model.entities.ProductRecommendation
import lc.fungee.IngrediCheck.model.entities.calculateMatch
import lc.fungee.IngrediCheck.model.entities.decoratedIngredientsList
import lc.fungee.IngrediCheck.ui.view.screens.analysis.AnalysisStatusChip
import lc.fungee.IngrediCheck.ui.view.screens.analysis.DecoratedIngredientsText
import lc.fungee.IngrediCheck.viewmodel.AnalysisPhase

class AnalysisResultViewModel : ViewModel() {
    data class UiState(
        val result: ProductRecommendation? = null,
        val fragments: List<DecoratedIngredientListFragment> = emptyList(),
        val hasIngredients: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun bind(product: Product, recs: List<IngredientRecommendation>) {
        val res = product.calculateMatch(recs)
        val frags = decoratedIngredientsList(product.ingredients, recs)
        _uiState.value = UiState(
            result = res,
            fragments = frags,
            hasIngredients = product.ingredients.isNotEmpty()
        )
    }
}

@Composable
fun AnalysisResultSection(
    product: Product,
    recommendations: List<IngredientRecommendation>,
    phase: AnalysisPhase = AnalysisPhase.Done,
    modifier: Modifier = Modifier
) {

    val vm = remember { AnalysisResultViewModel() }
    LaunchedEffect(product, recommendations) {
        vm.bind(product, recommendations)
    }
    val ui by vm.uiState.collectAsState()
    Column(modifier = modifier) {
        AnalysisStatusChip(phase = phase, result = ui.result)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Ingredients",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))
        if (ui.fragments.isNotEmpty()) {
            DecoratedIngredientsText(fragments = ui.fragments, recommendations = recommendations)
        } else {
            Text("No ingredient details available")
        }
    }
}

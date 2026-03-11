package lc.fungee.Ingredicheck.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScannerUiState(
    val hasCameraPermission: Boolean = false,
    val showPermissionRationale: Boolean = false,
    val isTorchOn: Boolean = false
)

class ScannerViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ScannerUiState(
            hasCameraPermission = savedStateHandle.get<Boolean>("hasCameraPermission") ?: false,
            showPermissionRationale = savedStateHandle.get<Boolean>("showPermissionRationale")
                ?: false,
            isTorchOn = savedStateHandle.get<Boolean>("isTorchOn") ?: false
        )
    )
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun onInitialPermissionCheck(granted: Boolean) {
        updatePermissionState(
            hasPermission = granted,
            showRationale = _uiState.value.showPermissionRationale
        )
    }

    fun onPermissionResult(granted: Boolean) {
        updatePermissionState(
            hasPermission = granted,
            showRationale = !granted
        )
    }

    private fun updatePermissionState(
        hasPermission: Boolean,
        showRationale: Boolean
    ) {
        _uiState.value = _uiState.value.copy(
            hasCameraPermission = hasPermission,
            showPermissionRationale = showRationale
        )
        savedStateHandle["hasCameraPermission"] = hasPermission
        savedStateHandle["showPermissionRationale"] = showRationale
    }

    fun setTorchEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isTorchOn = enabled)
        savedStateHandle["isTorchOn"] = enabled
    }
}
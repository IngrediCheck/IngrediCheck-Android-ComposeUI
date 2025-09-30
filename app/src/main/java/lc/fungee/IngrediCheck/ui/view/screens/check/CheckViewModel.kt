package lc.fungee.IngrediCheck.ui.view.screens.check

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.model.model.ImageInfo
import lc.fungee.IngrediCheck.model.repository.PreferenceRepository
import lc.fungee.IngrediCheck.domain.usecase.DetectBarcodeUseCase
import lc.fungee.IngrediCheck.domain.usecase.RecognizeTextUseCase
import lc.fungee.IngrediCheck.domain.usecase.UploadImageUseCase
import lc.fungee.IngrediCheck.model.source.image.ImageCache
import java.io.File

sealed class CheckUiState {
    data object Idle : CheckUiState()
    data object Processing : CheckUiState()
    data class AnalysisReady(val images: List<ImageInfo>, val barcode: String?) : CheckUiState()
    data class Error(val message: String) : CheckUiState()
}

sealed class CheckEvent {
    data class ShowToast(val message: String) : CheckEvent()
    data class NavigateToAnalysis(val images: List<ImageInfo>, val barcode: String?) : CheckEvent()
}

class CheckViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val recognizeText: RecognizeTextUseCase,
    private val detectBarcode: DetectBarcodeUseCase,
    private val uploadImage: UploadImageUseCase,
    private val functionsBaseUrl: String,
    private val anonKey: String,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckUiState>(CheckUiState.Idle)
    val uiState: StateFlow<CheckUiState> = _uiState

    // Buffered to avoid missing one-off events if collector is late
    private val _events = MutableSharedFlow<CheckEvent>(replay = 0, extraBufferCapacity = 64)
    val events: SharedFlow<CheckEvent> = _events

    fun onPhotoCaptured(file: File) {
        viewModelScope.launch {
            try {
                Log.d("CheckVM", "onPhotoCaptured: start, fileSize=${file.length()}")
                _uiState.value = CheckUiState.Processing

                // Navigate immediately to the Analysis sheet to show the white loading screen
                // We pass empty images/barcode so AnalysisScreen shows LoadingContent right away
                _events.tryEmit(CheckEvent.NavigateToAnalysis(images = emptyList(), barcode = null))

                val token = preferenceRepository.currentToken()
                if (token.isNullOrBlank()) {
                    Log.w("CheckVM", "No token found; prompting sign-in")
                    _events.tryEmit(CheckEvent.ShowToast("Please sign in and try again."))
                    _uiState.value = CheckUiState.Error("Not authenticated")
                    return@launch
                }

                val ocrDeferred = async { runCatching { recognizeText(file, appContext) }.getOrDefault("") }
                val codeDeferred = async { runCatching { detectBarcode(file, appContext) }.getOrNull() }
                val uploadDeferred = async {
                    runCatching { uploadImage(file, token, functionsBaseUrl, anonKey) }.getOrNull()
                }

                val ocrText = ocrDeferred.await()
                val barcode = codeDeferred.await()
                val imageHash = uploadDeferred.await()

                if (imageHash == null) {
                    Log.e("CheckVM", "Upload failed; imageHash=null")
                    _events.tryEmit(CheckEvent.ShowToast("Image upload failed. Please try again."))
                    _uiState.value = CheckUiState.Error("Upload failed")
                    return@launch
                }

                Log.d("CheckVM", "OCR length=${ocrText.length}, barcode=${barcode}, hash=$imageHash")
                // Optimistically cache the just-captured photo under the hash so UI can load instantly
                runCatching { ImageCache.cacheFromFile(appContext, imageHash, file) }
                    .onFailure { Log.w("CheckVM", "cacheFromFile failed for $imageHash", it) }
                val imageInfo = ImageInfo(
                    imageFileHash = imageHash,
                    imageOCRText = ocrText,
                    barcode = barcode
                )
                _uiState.value = CheckUiState.AnalysisReady(images = listOf(imageInfo), barcode = barcode)
                Log.d("CheckVM", "Emitting NavigateToAnalysis event")
                _events.tryEmit(CheckEvent.NavigateToAnalysis(images = listOf(imageInfo), barcode = barcode))
            } catch (t: Throwable) {
                Log.e("CheckVM", "Unexpected error in onPhotoCaptured", t)
                _events.tryEmit(CheckEvent.ShowToast("Something went wrong while processing."))
                _uiState.value = CheckUiState.Error(t.message ?: "Processing failed")
            }
        }
    }

    fun onBarcodeScanned(value: String) {
        viewModelScope.launch {
            Log.d("CheckVM", "onBarcodeScanned: $value")
            _events.tryEmit(CheckEvent.NavigateToAnalysis(emptyList(), value))
        }
    }

    fun reset() {
        Log.d("CheckVM", "reset() called - returning to Idle")
        _uiState.value = CheckUiState.Idle
    }
}

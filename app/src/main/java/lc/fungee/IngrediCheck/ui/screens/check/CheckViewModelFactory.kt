package lc.fungee.IngrediCheck.ui.screens.check

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.jan.supabase.SupabaseClient
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import lc.fungee.IngrediCheck.di.AppContainer
import lc.fungee.IngrediCheck.domain.usecase.DetectBarcodeUseCase
import lc.fungee.IngrediCheck.domain.usecase.RecognizeTextUseCase
import lc.fungee.IngrediCheck.domain.usecase.UploadImageUseCase

class CheckViewModelFactory(
    private val container: AppContainer,
    private val supabaseClient: SupabaseClient,
    private val functionsBaseUrl: String,
    private val anonKey: String,
    private val appContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckViewModel::class.java)) {
            val prefRepo = PreferenceRepository(
                context = appContext,
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
            val recognizeText = RecognizeTextUseCase(container.textRecognizer)
            val detectBarcode = DetectBarcodeUseCase(container.barcodeScanner)
            val uploadImage = UploadImageUseCase(container.storageService)
            return CheckViewModel(
                preferenceRepository = prefRepo,
                recognizeText = recognizeText,
                detectBarcode = detectBarcode,
                uploadImage = uploadImage,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey,
                appContext = appContext
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

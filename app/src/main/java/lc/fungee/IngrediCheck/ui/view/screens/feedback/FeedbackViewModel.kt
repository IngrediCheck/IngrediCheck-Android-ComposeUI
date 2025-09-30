package lc.fungee.IngrediCheck.ui.view.screens.feedback

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.model.model.feedback.FeedbackData
import lc.fungee.IngrediCheck.model.model.feedback.FeedbackImageData
import lc.fungee.IngrediCheck.model.repository.FeedbackRepository
import lc.fungee.IngrediCheck.model.repository.FeedbackSubmitResult
import lc.fungee.IngrediCheck.model.repository.PreferenceRepository
import lc.fungee.IngrediCheck.model.source.image.ImageCache
import lc.fungee.IngrediCheck.model.source.remote.StorageService
import lc.fungee.IngrediCheck.domain.usecase.DetectBarcodeUseCase
import lc.fungee.IngrediCheck.domain.usecase.RecognizeTextUseCase
import lc.fungee.IngrediCheck.domain.usecase.UploadImageUseCase
import java.io.File
import java.util.UUID

data class FeedbackPhotoUi(
    val tempId: String,                // stable key before upload finishes
    val imageFileHash: String? = null, // becomes non-null after upload
    val localFile: File? = null,       // used for immediate thumbnail
    val imageOCRText: String? = null,
    val barcode: String? = null,
    val isProcessing: Boolean = true
)

data class FeedbackUiState(
    val reasons: List<String> = emptyList(),
    val note: String = "",
    val allowPhotos: Boolean = false,
    val photos: List<FeedbackPhotoUi> = emptyList(),
    val isSubmitting: Boolean = false,
    val processingCaptures: Int = 0,
    val error: String? = null
)

class FeedbackViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val feedbackRepository: FeedbackRepository,
    private val recognizeText: RecognizeTextUseCase,
    private val detectBarcode: DetectBarcodeUseCase,
    private val uploadImage: UploadImageUseCase,
    private val storageService: StorageService,
    private val functionsBaseUrl: String,
    private val anonKey: String,
    private val appContext: Context
) : ViewModel() {

    var ui by mutableStateOf(FeedbackUiState())
        private set

    private val _localFiles = mutableListOf<File>()
    private val activeJobs = mutableListOf<Job>()

    fun setAllowPhotos(value: Boolean) { ui = ui.copy(allowPhotos = value) }
    fun setReasons(list: List<String>) { ui = ui.copy(reasons = list) }
    fun toggleReason(reason: String) {
        val cur = ui.reasons.toMutableList()
        if (cur.contains(reason)) cur.remove(reason) else cur.add(reason)
        ui = ui.copy(reasons = cur)
    }
    fun setNote(value: String) { ui = ui.copy(note = value) }

    fun clear() {
        ui = FeedbackUiState(allowPhotos = ui.allowPhotos)
        _localFiles.clear()
    }

    fun onCapture(file: File) {
        // Immediately show as a processing item with local thumbnail
        val id = UUID.randomUUID().toString()
        ui = ui.copy(
            photos = ui.photos + FeedbackPhotoUi(tempId = id, localFile = file, isProcessing = true),
            processingCaptures = ui.processingCaptures + 1
        )

        val job = viewModelScope.launch {
            val token = preferenceRepository.currentToken() ?: return@launch
            try {
                coroutineScope {
                    val ocrDef = async<String>(Dispatchers.IO) { runCatching { recognizeText(file, appContext) }.getOrDefault("") }
                    val barcodeDef = async<String?>(Dispatchers.IO) { runCatching { detectBarcode(file, appContext) }.getOrNull() }
                    val uploadDef = async<String?>(Dispatchers.IO) { runCatching { uploadImage(file, token, functionsBaseUrl, anonKey) }.getOrNull() }

                    val ocr = ocrDef.await()
                    val barcode = barcodeDef.await()
                    val imageHash = uploadDef.await()

                    if (imageHash != null) {
                        // Cache by hash for future small/medium thumbs
                        runCatching { ImageCache.cacheFromFile(appContext, imageHash, file) }
                        _localFiles.add(file)
                    }

                    // Update the placeholder entry
                    ui = ui.copy(
                        photos = ui.photos.map {
                            if (it.tempId == id) it.copy(
                                imageFileHash = imageHash,
                                imageOCRText = ocr,
                                barcode = barcode,
                                isProcessing = false
                            ) else it
                        }
                    )
                }
            } finally {
                ui = ui.copy(processingCaptures = (ui.processingCaptures - 1).coerceAtLeast(0))
            }
        }
        activeJobs += job
        job.invokeOnCompletion { activeJobs.remove(job) }
    }

    suspend fun submit(clientActivityId: String): FeedbackSubmitResult {
        val token = preferenceRepository.currentToken() ?: return FeedbackSubmitResult.Unauthorized
        ui = ui.copy(isSubmitting = true, error = null)
        return try {
            // Ensure all background tasks completed
            val jobs = activeJobs.toList()
            jobs.joinAll()

            val imagesList = ui.photos.mapNotNull { p ->
                p.imageFileHash?.let { hash -> FeedbackImageData(hash, p.imageOCRText ?: "", p.barcode) }
            }
            val data = FeedbackData(
                reasons = ui.reasons,
                note = if (ui.note.isEmpty()) null else ui.note,
                images = if (imagesList.isEmpty()) null else imagesList,
                rating = 0
            )
            val result = feedbackRepository.submitFeedback(
                accessToken = token,
                clientActivityId = clientActivityId,
                data = data
            )
            ui = ui.copy(isSubmitting = false)
            result
        } catch (t: Throwable) {
            ui = ui.copy(isSubmitting = false, error = t.message)
            FeedbackSubmitResult.Failure(t.message)
        }
    }

    suspend fun deleteUploadedImagesOnCancel() {
        val token = preferenceRepository.currentToken() ?: return
        // Best-effort: delete only uploaded objects (with hash)
        ui.photos.forEach { img ->
            val hash = img.imageFileHash
            if (hash != null) runCatching { storageService.deleteObject(hash, token, functionsBaseUrl, anonKey) }
        }
        clear()
    }

    fun removeImage(hash: String) {
        viewModelScope.launch {
            val token = preferenceRepository.currentToken() ?: return@launch
            // Best-effort delete
            runCatching { storageService.deleteObject(hash, token, functionsBaseUrl, anonKey) }
            ui = ui.copy(photos = ui.photos.filterNot { it.imageFileHash == hash })
        }
    }

    fun removeLocalPhoto(tempId: String) {
        // Remove a pending photo that hasn't finished uploading yet
        ui = ui.copy(photos = ui.photos.filterNot { it.tempId == tempId })
    }

    fun setError(message: String?) { ui = ui.copy(error = message) }

    fun clearImages() {
        viewModelScope.launch {
            val token = preferenceRepository.currentToken() ?: return@launch
            ui.photos.forEach { img ->
                val hash = img.imageFileHash
                if (hash != null) runCatching { storageService.deleteObject(hash, token, functionsBaseUrl, anonKey) }
            }
            ui = ui.copy(photos = emptyList())
        }
    }
}

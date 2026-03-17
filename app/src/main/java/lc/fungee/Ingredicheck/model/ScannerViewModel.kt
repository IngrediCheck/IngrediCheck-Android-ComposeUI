package lc.fungee.Ingredicheck.model

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import lc.fungee.Ingredicheck.AppConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

data class ScannerUiState(
    val hasCameraPermission: Boolean = false,
    val showPermissionRationale: Boolean = false,
    val isTorchOn: Boolean = false
)

class ScannerViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val client = OkHttpClient()
    private var sseEventSource: EventSource? = null

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val _uiState = MutableStateFlow(
        ScannerUiState(
            hasCameraPermission = savedStateHandle.get<Boolean>("hasCameraPermission") ?: false,
            showPermissionRationale = savedStateHandle.get<Boolean>("showPermissionRationale")
                ?: false,
            isTorchOn = savedStateHandle.get<Boolean>("isTorchOn") ?: false
        )
    )
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _scanResult = MutableStateFlow<ScanResponse?>(null)
    val scanResult: StateFlow<ScanResponse?> = _scanResult.asStateFlow()

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

    /**
     * Start an SSE stream for the given barcode. Caller must provide a valid access token.
     */
    fun startBarcodeStream(barcode: String, accessToken: String) {
        // 1. Normalize barcode: UPC-A (12 digits) should be sent as EAN-13 (13 digits) to match iOS/Backend
        val normalizedBarcode = if (barcode.length == 11) {
            "00$barcode"
        } else if (barcode.length == 12) {
            "0$barcode"
        } else {
            barcode
        }

        // 2. DONT RESTART if we are already processing this exact barcode 
        // Or if the results is already 'done' for this barcode
        val currentResult = _scanResult.value
        if (currentResult?.barcode == normalizedBarcode && 
            (currentResult.state == "fetching_product_info" || currentResult.state == "analyzing")
        ) {
            Log.d("ScannerViewModel", "Already processing barcode $normalizedBarcode. skipping.")
            return
        }

        // 3. Cancel any existing stream
        sseEventSource?.cancel()

        val base = AppConfig.flyIOBaseURL
        val baseUrl = if (base.endsWith("/")) base.dropLast(1) else base
        val url = "$baseUrl/v2/scan/barcode"

        Log.d("ScannerViewModel", "Starting barcode stream. barcode=$normalizedBarcode (raw=$barcode) url=$url")

        val bodyJson = """{"barcode": "$normalizedBarcode"}"""
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $accessToken")
            .header("Accept", "text/event-stream")
            .post(bodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                Log.d("ScannerViewModel", "SSE onEvent: type=$type id=$id data=$data")
                // iOS uses type == "scan" – mirror that
                if (type == null || type == "scan") {
                    runCatching {
                        json.decodeFromString(ScanResponse.serializer(), data)
                    }.onSuccess { response ->
                        Log.d(
                            "ScannerViewModel",
                            "Decoded ScanResponse: id=${response.id} state=${response.state} " +
                                "name=${response.productInfo?.name} brand=${response.productInfo?.brand}"
                        )
                        _scanResult.value = response
                    }.onFailure { e ->
                        Log.e("ScannerViewModel", "Failed to decode ScanResponse", e)
                    }
                }
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                val errorMsg = t?.message ?: response?.message ?: "Unknown error"
                Log.e(
                    "ScannerViewModel",
                    "SSE onFailure: throwable=$errorMsg httpCode=${response?.code}"
                )

                // 3. EXTREMELY IMPORTANT: If the stream was cancelled by US, do not show an error state
                // This happens when the detecter sees a barcode and starts/restarts while we are cleanup
                val isCancelled = errorMsg.contains("Canceled", ignoreCase = true) || 
                                 errorMsg.contains("CANCEL", ignoreCase = true)
                
                if (isCancelled) {
                    Log.d("ScannerViewModel", "Ignoring cancellation failure: $errorMsg")
                    return
                }

                _scanResult.value = ScanResponse(
                    id = "error",
                    state = "error",
                    error = errorMsg
                )
            }
        }

        sseEventSource = EventSources.createFactory(client)
            .newEventSource(request, listener)
    }
}

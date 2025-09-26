package lc.fungee.IngrediCheck.di

import android.content.Context
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import lc.fungee.IngrediCheck.data.source.mlkit.MlKitTextRecognizer
import lc.fungee.IngrediCheck.data.source.mlkit.MlKitBarcodeScanner
import lc.fungee.IngrediCheck.data.source.mlkit.TextRecognizerService
import lc.fungee.IngrediCheck.data.source.mlkit.BarcodeScannerService
import lc.fungee.IngrediCheck.data.source.remote.StorageService
import lc.fungee.IngrediCheck.data.source.remote.SupabaseStorageService

class AppContainer(context: Context) {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    // MLKit wrappers used by use-cases / VMs
    val textRecognizer: TextRecognizerService = MlKitTextRecognizer()
    val barcodeScanner: BarcodeScannerService = MlKitBarcodeScanner()

    // Remote storage (Supabase)
    val storageService: StorageService = SupabaseStorageService(okHttpClient)
}

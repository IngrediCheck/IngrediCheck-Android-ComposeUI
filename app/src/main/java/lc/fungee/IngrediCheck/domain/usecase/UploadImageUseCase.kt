package lc.fungee.IngrediCheck.domain.usecase

import java.io.File
import lc.fungee.IngrediCheck.data.source.remote.StorageService

class UploadImageUseCase(private val storage: StorageService) {
    suspend operator fun invoke(
        file: File,
        accessToken: String,
        functionsBaseUrl: String,
        anonKey: String
    ): String? {
        val bytes = file.readBytes() // consider compression later if needed
        return storage.uploadJpeg(
            bytes = bytes,
            accessToken = accessToken,
            functionsBaseUrl = functionsBaseUrl,
            anonKey = anonKey
        )
    }
}

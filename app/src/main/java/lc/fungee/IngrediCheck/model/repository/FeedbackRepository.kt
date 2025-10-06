package lc.fungee.IngrediCheck.model.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import lc.fungee.IngrediCheck.model.entities.SafeEatsEndpoint
import lc.fungee.IngrediCheck.model.dto.feedback.FeedbackData
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

sealed class FeedbackSubmitResult {
    data object Success : FeedbackSubmitResult()
    data object Unauthorized : FeedbackSubmitResult()
    data class Failure(val message: String?) : FeedbackSubmitResult()
}

class FeedbackRepository(
    private val okHttp: OkHttpClient,
    private val functionsBaseUrl: String,
    private val anonKey: String,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true // ensure default fields like rating=0 are included
    }
) {
    suspend fun submitFeedback(
        accessToken: String,
        clientActivityId: String,
        data: FeedbackData
    ): FeedbackSubmitResult = withContext(Dispatchers.IO) {
        try {
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.FEEDBACK.format()}"
            val feedbackJson = json.encodeToString(data)

            Log.d("FeedbackRepo", "payload=" + feedbackJson)

            val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("clientActivityId", clientActivityId)
                .addFormDataPart("feedback", feedbackJson)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(multipart)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("apikey", anonKey)
                .build()

            okHttp.newCall(request).execute().use { resp ->
                val code = resp.code
                val body = resp.body?.string().orEmpty()
                Log.d("FeedbackRepo", "POST /feedback code=$code body=${body.take(200)}")
                when (code) {
                    201 -> FeedbackSubmitResult.Success
                    401 -> FeedbackSubmitResult.Unauthorized
                    else -> FeedbackSubmitResult.Failure("Bad response: $code ${body.take(200)}")
                }
            }
        } catch (e: Exception) {
            Log.e("FeedbackRepo", "submitFeedback failed", e)
            FeedbackSubmitResult.Failure(e.message)
        }
    }
}

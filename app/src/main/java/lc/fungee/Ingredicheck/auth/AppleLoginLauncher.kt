package lc.fungee.Ingredicheck.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberAppleLoginLauncher(
    activity: Activity?,
    authViewModel: AuthViewModel
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    val data: Intent? = result.data

    if (result.resultCode != Activity.RESULT_OK || data == null) {
        Log.e("AppleSignIn", "Apple login canceled or no data returned")
        authViewModel.setError("Apple login was cancelled or failed")
        return@rememberLauncherForActivityResult
    }

    val error = data.getStringExtra("error")
    val errorDescription = data.getStringExtra("error_description")
    if (!error.isNullOrBlank()) {
        Log.e("AppleSignIn", "Apple login failed. error=$error description=$errorDescription")
        authViewModel.setError(errorDescription ?: error)
        return@rememberLauncherForActivityResult
    }

    val idToken = data.getStringExtra("id_token")
    val code = data.getStringExtra("code")

    when {
        !idToken.isNullOrBlank() -> {
            Log.d("AppleSignIn", "Apple login returned id_token")
            authViewModel.signInWithAppleIdToken(idToken)
        }

        !code.isNullOrBlank() -> {
            Log.d("AppleSignIn", "Apple login returned code; exchanging for session")
            authViewModel.signInWithAppleCode(code)
        }

        else -> {
            Log.e("AppleSignIn", "Apple login returned no id_token or code")
            authViewModel.setError("Apple login returned no id_token")
        }
    }
}

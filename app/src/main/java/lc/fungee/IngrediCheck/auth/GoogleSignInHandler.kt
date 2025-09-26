package lc.fungee.IngrediCheck.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun rememberGoogleSignInLauncherUnused(
    activity: Activity,
    viewModel: AppleAuthViewModel
)
= rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    val data = result.data
    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
    try {
        val account = task.getResult(ApiException::class.java)
        val idToken = account.idToken
        if (idToken != null) {
            viewModel.signInWithGoogleIdToken(idToken, activity)
        }
    } catch (e: Exception) {
        Log.e("GoogleSignIn", "Sign-in failed", e)
    }
}

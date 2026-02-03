package lc.fungee.Ingredicheck.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun rememberGoogleSignInLauncher(
    activity: Activity?,
    authViewModel: AuthViewModel
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    val data = result.data
    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
    try {
        val account = task.getResult(ApiException::class.java)
        val idToken = account.idToken
        if (idToken != null) {
            authViewModel.signInWithGoogleIdToken(idToken)
        } else {
            Log.e(
                "GoogleSignIn",
                "idToken is null. package=${activity?.packageName}, email=${account.email}"
            )
        }
    } catch (e: ApiException) {
        Log.e(
            "GoogleSignIn",
            "Sign-in failed. statusCode=${e.statusCode}, statusMessage=${e.statusMessage}, package=${activity?.packageName}",
            e
        )
    } catch (e: Exception) {
        Log.e(
            "GoogleSignIn",
            "Sign-in failed (non-ApiException). package=${activity?.packageName}",
            e
        )
    }
}

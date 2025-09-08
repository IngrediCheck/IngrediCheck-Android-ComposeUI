package lc.fungee.IngrediCheck.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Deprecated("Use GoogleAuthClient in GoogleAuthClient.kt")
object GoogleAuthClientLegacy {
    fun getClient(context: Context): GoogleSignInClient {
        return GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("478832614549-s0ucvjfchkikp57vj5u0bc29jqthme63.apps.googleusercontent.com")
                .requestEmail()
                .build()
        )
    }
}

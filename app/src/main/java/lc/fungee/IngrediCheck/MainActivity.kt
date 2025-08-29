

package lc.fungee.IngrediCheck
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lc.fungee.IngrediCheck.ui.theme.IngrediCheckTheme
import lc.fungee.IngrediCheck.auth.AppleAuthRepository
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.auth.GoogleAuthClient
import lc.fungee.IngrediCheck.auth.rememberGoogleSignInLauncher
import androidx.compose.runtime.remember


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IngrediCheckTheme {
                val context = this
                val repository = remember {
                    AppleAuthRepository(
                        context = context,
                        supabaseUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co",
                        supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI"
                    )
                }



                

                val viewModel = remember { AppleAuthViewModel(repository) }
                val googleSignInClient = GoogleAuthClient.getClient(context)
                val googleSignInLauncher = rememberGoogleSignInLauncher(this, viewModel)


                AppNavigation(
                    viewModel = viewModel,
                    googleSignInLauncher = googleSignInLauncher,
                    googleSignInClient = googleSignInClient
                )
            }

        }
    }
}


//package lc.fungee.IngrediCheck
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import lc.fungee.IngrediCheck.ui.theme.IngrediCheckTheme
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import androidx.activity.result.contract.ActivityResultContracts
//import lc.fungee.IngrediCheck.onboarding.WelcomeScreen
//import androidx.compose.runtime.remember
//
//class MainActivity : ComponentActivity() {
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private var onGoogleIdTokenReceived: ((String) -> Unit)? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        // TODO: Replace with your actual Google OAuth client ID
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("478832614549-s0ucvjfchkikp57vj5u0bc29jqthme63.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        lateinit var viewModel: lc.fungee.IngrediCheck.auth.AppleAuthViewModel
//
//        val googleSignInLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            val data = result.data
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
//                val idToken = account.idToken
//                if (idToken != null) {
//                    viewModel.signInWithGoogleIdToken(idToken)
//                }
//            } catch (e: Exception) {
//                Log.e("GoogleSignIn", "Sign-in failed", e)
//            }
//        }
//
//        setContent {
//            IngrediCheckTheme {
//                // Create repository and viewModel here so we can access viewModel in launcher
//                val context = this
//                val repository = remember {
//                    lc.fungee.IngrediCheck.auth.AppleAuthRepository(
//                        context = context,
//                        supabaseUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co",
//                        supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI"
//                    )
//                }
//                viewModel = remember { lc.fungee.IngrediCheck.auth.AppleAuthViewModel(repository) }
//
//                WelcomeScreen(
//                    onGoogleSignIn = {
//                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
//                    },
//                    viewModel = viewModel
//                )
//
//
//            }
//        }
//    }
//
//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        intent.data?.let { uri: Uri ->
//            val code = uri.getQueryParameter("code")
//            val idToken = uri.getQueryParameter("id_token")
//            Log.d("AppleRedirect", "code: $code, id_token: $idToken, uri: $uri")
//            // TODO: Pass code or idToken to your ViewModel or repository for Supabase login
//        }
//    }
//}
//

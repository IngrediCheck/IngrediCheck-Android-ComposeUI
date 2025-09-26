package lc.fungee.IngrediCheck.ui.screens.setting
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import android.content.Context
import android.widget.Toast
import android.webkit.CookieManager
import android.webkit.WebStorage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.*
import lc.fungee.IngrediCheck.data.repository.PreferenceViewModel
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

enum class ConfirmAction {
    NONE, DELETE_ACCOUNT, RESET_GUEST
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    preferenceViewModel: PreferenceViewModel,
    onDismiss: () -> Unit = {},
    supabaseClient: SupabaseClient,
    onRequireReauth: () -> Unit,
    viewModel: AppleAuthViewModel,
    googleSignInClient: GoogleSignInClient
) {

    val autoScan by preferenceViewModel.autoScanFlow.collectAsState(initial = false)
    var selectedUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("user_session", Context.MODE_PRIVATE) }
    val loginProvider = remember { sharedPrefs.getString("login_provider", null) }
    val isGuest = loginProvider.isNullOrBlank() || loginProvider == "anonymous"
    val coroutineScope = rememberCoroutineScope()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showDeleteGuestDialog by remember { mutableStateOf(false) }
    var confirmAction by remember { mutableStateOf(ConfirmAction.NONE) }
    fun clearWebCookies() {
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebStorage.getInstance().deleteAllData()
        } catch (_: Throwable) {}
    }
    fun clearAllSession()
    {
        coroutineScope.launch {
            // Centralize Supabase sign-out via ViewModel
            try { viewModel.signOut(context) } catch (_: Exception) {}
            // Google: sign out and revoke if applicable
            try { googleSignInClient.signOut() } catch (_: Exception) {}
            try { googleSignInClient.revokeAccess() } catch (_: Exception) {}
            // Web cookies and local data
            clearWebCookies()
            preferenceViewModel.clearAllLocalData()
            sharedPrefs.edit().clear().apply()
            onRequireReauth()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        SettingHeader(onDismiss = onDismiss)

        Spacer(Modifier.height(16.dp))

        // 🔹 Section: Settings
        SettingSection(title = "SETTINGS") {
            SwitchRow(
                text = "Start Scanning on App Start",
                checked = autoScan,
                onCheckedChange = {
                    preferenceViewModel.setAutoScan(it)
                    // Set one-shot pending flag so next app start auto-opens scanner once
                    preferenceViewModel.setAutoScanPending(it)
                }
            )
        }

        Spacer(Modifier.height(16.dp))


        SettingSection(title = "ACCOUNT") {
            if (isGuest) {
                // Guest/anonymous flow: only allow clearing local data and restart
                IconRow(
                    "Delete & Restart App",
                    Icons.Default.Warning,
                    tint = AppColors.ErrorStrong,
                    tint2 = AppColors.ErrorStrong
                ) { confirmAction = ConfirmAction.RESET_GUEST }
            } else {
                // Authenticated user: show Sign Out and Delete Data & Account
                IconRow(
                    "Sign Out",
                    Icons.Default.ExitToApp,
                    tint = AppColors.Brand
                ) { clearAllSession()}
                IconRow(
                    "Delete Data & Account",
                    Icons.Default.Delete,
                    tint = AppColors.ErrorStrong,
                    tint2 = AppColors.ErrorStrong
                ) { confirmAction = ConfirmAction.DELETE_ACCOUNT }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Section: About
        SettingSection(title = "ABOUT") {
            IconRow(
                "About me",
                Icons.Default.AccountCircle,
//                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/about" }

//            IconRow(
//                "Tip Jar",
//                Icons.Default.FavoriteBorder,
////                R.drawable.rightbackbutton
//            ) { selectedUrl = "https://www.ingredicheck.app/tipjar" }

            IconRow(
                "Help",
                Icons.Default.Info,
//                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/about" }

            IconRow(
                "Terms of Use",
                Icons.Default.AddCircle,
//                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/terms-conditions" }

            IconRow(
                "Privacy Policy",
                Icons.Default.Lock,
//                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/privacy-policy" }

            IconRow(
                "IngrediCheck for Android 1.0.(38)",
                Icons.Default.Star,
//                null,
                showDivider = false
            )
        }

    }


    if (confirmAction != ConfirmAction.NONE) {
        var title = ""
        var confirmText = ""
        var onConfirm: () -> Unit = {}

        when (confirmAction) {
            ConfirmAction.DELETE_ACCOUNT -> {
                title = "Your Data Cannot Be Recovered"
                confirmText = "I Understand"
                onConfirm = {
                    coroutineScope.launch {
                        // First: try remote account deletion (Edge Function must be deployed server-side)
                        runCatching { preferenceViewModel.deleteAccountRemote() }
                        clearAllSession()
                    }
                }
            }
            ConfirmAction.RESET_GUEST -> {
                title = "Your Data Cannot Be Recovered"
                confirmText = "I Understand"
                onConfirm = {
                    coroutineScope.launch {
                        runCatching { viewModel.signOut(context) }
                        preferenceViewModel.clearAllLocalData()
                        sharedPrefs.edit().clear().apply()
                        clearWebCookies()
                        onRequireReauth()
                    }
                }
            }
            else -> { /* no-op */ }
        }

        AlertDialog(
            onDismissRequest = { confirmAction = ConfirmAction.NONE },
            title = { Text(title) },

            confirmButton = {
                TextButton(onClick = {
                    confirmAction = ConfirmAction.NONE
                    onConfirm()
                }) {
                    Text(confirmText,color = AppColors.Brand)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmAction = ConfirmAction.NONE }) {
                    Text("Cancel",color = AppColors.Brand
                    )
                }
            }
            ,  containerColor = Greyscale100Alt,
            textContentColor = Color.Black// 👈 your custom background
        )
    }


//    // Confirmation dialogs
//    if (showSignOutDialog) {
//        AlertDialog(
//            onDismissRequest = { showSignOutDialog = false },
//            title = { Text("Sign out?") },
//            text = { Text("You will be signed out and your local data will be cleared.") },
//            confirmButton = {
//                TextButton(onClick = {
//                    showSignOutDialog = false
//                    coroutineScope.launch {
//                        try { supabaseClient.auth.signOut() } catch (_: Exception) {}
//                        try { googleSignInClient.signOut() } catch (_: Exception) {}
//                        try { googleSignInClient.revokeAccess() } catch (_: Exception) {}
//                        try { viewModel.clearSession(context) } catch (_: Exception) {}
//                        try { viewModel.resetState() } catch (_: Exception) {}
//                        clearWebCookies()
//                        preferenceViewModel.clearAllLocalData()
//                        sharedPrefs.edit().clear().apply()
//                        onRequireReauth()
//                    }
//                }) { Text("Sign Out") }
//            },
//            dismissButton = {
//                TextButton(onClick = { showSignOutDialog = false }) { Text("Cancel") }
//            }
//        )
//    }

//    if (showDeleteAccountDialog) {
//        AlertDialog(
//            onDismissRequest = { showDeleteAccountDialog = false },
//            title = { Text("Your Data Cannot Be recovered") },
////            text = { Text("This will clear your local data and sign you out.") },
//            confirmButton = {
//                TextButton(onClick = {
//                    showDeleteAccountDialog = false
//                    coroutineScope.launch {
//                        // First: try remote account deletion (Edge Function must be deployed server-side)
//                        val remoteDeleted = try {
//                            preferenceViewModel.deleteAccountRemote()
//                        } catch (_: Exception) { false }
//
//                        // Then: proceed with provider sign-outs and local wipe
//                        try { supabaseClient.auth.signOut() } catch (_: Exception) {}
//                        try { googleSignInClient.signOut() } catch (_: Exception) {}
//                        try { googleSignInClient.revokeAccess() } catch (_: Exception) {}
//                        try { viewModel.clearSession(context) } catch (_: Exception) {}
//                        try { viewModel.resetState() } catch (_: Exception) {}
//                        clearWebCookies()
//                        preferenceViewModel.clearAllLocalData()
//                        sharedPrefs.edit().clear().apply()
//                        onRequireReauth()
//                    }
//                }) { Text("i Understand") }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("Cancel") }
//            }
//        )
//    }
//
//    if (showDeleteGuestDialog) {
//        AlertDialog(
//            onDismissRequest = { showDeleteGuestDialog = false },
//            title = { Text("Delete local data?") },
//            text = { Text("This will clear all local data and restart the app.") },
//            confirmButton = {
//                TextButton(onClick = {
//                    showDeleteGuestDialog = false
//                    coroutineScope.launch {
//                        // For guests, just clear local data and session prefs
//                        preferenceViewModel.clearAllLocalData()
//                        sharedPrefs.edit().clear().apply()
//                        clearWebCookies()
//                        try { viewModel.resetState() } catch (_: Exception) {}
//                        onRequireReauth()
//                    }
//                }) { Text("Delete & Restart") }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDeleteGuestDialog = false }) { Text("Cancel") }
//            }
//        )
//    }

    selectedUrl?.let { url ->
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        // Add this to preload the WebView content
        LaunchedEffect(url) {
            // Small delay to ensure smooth animation
            kotlinx.coroutines.delay(100)
        }

        ModalBottomSheet(
            onDismissRequest = { selectedUrl = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            sheetState = sheetState,
            dragHandle = null, // Remove default drag handle for cleaner look
//            windowInsets = WindowInsets(0) // Remove default insets
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.94f)
                    .background(Color.White) // Ensure white background
            ) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.Close, // Use Close icon instead
                        contentDescription = "Close",
                        tint = Grey75,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { selectedUrl = null }
                    )
                }

                // WebView content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    WebViewScreen(url = url)
                }
            }
        }
    }
}

@Composable
private fun SettingHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            text = "SETTINGS",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp,
                color = AppColors.Neutral700,
                lineHeight = 22.sp
            )
        )
        Spacer(Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.trailingicon),
            contentDescription = "Dismiss",
            tint = Greyscale400,
            modifier = Modifier
                .size(18.dp)
                .clickable { onDismiss() }
        )
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 0.05.em,
                textAlign = TextAlign.Start,
                color = Greyscale400
            ),
            modifier = Modifier.padding(start = 15.dp, bottom = 5.dp)
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(White)
        ) {
            content()
        }
    }
}

@Composable
private fun SwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                letterSpacing = (-0.41).sp,
                color = AppColors.Neutral700,
                lineHeight = 22.sp
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            interactionSource = remember { MutableInteractionSource() },
            modifier = Modifier
                .height(31.dp)
                .width(51.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.Brand,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppColors.SurfaceMuted,
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun IconRow(
    text: String,
    leadingVector: ImageVector,
    tint: Color = Color.Black, //
    tint2: Color =  AppColors.Brand,
    showDivider: Boolean = true, //
    onClick: (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick?.invoke() }
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = leadingVector,
                contentDescription = null,
                tint = tint2
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    //   letterSpacing = (-0.41).sp,
                    color = tint
//                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
//
            if(showDivider) {

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    modifier = Modifier.size(20.dp),
                    contentDescription = null,
                    tint = Grey75
                )

            }
        }

        // 👇 Divider only if needed
        if (showDivider) {
            Divider(
                color = AppColors.Divider,
                thickness = 2.dp,
                modifier = Modifier.padding(start = 35.dp)
            )
        }
    }
}

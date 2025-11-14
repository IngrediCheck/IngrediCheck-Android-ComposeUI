package lc.fungee.IngrediCheck.ui.view.screens.setting
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import android.webkit.CookieManager
import android.webkit.WebStorage
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.*
import lc.fungee.IngrediCheck.viewmodel.PreferenceViewModel
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import lc.fungee.IngrediCheck.model.utils.AppConstants
import android.widget.Toast


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
    val sharedPrefs = remember { context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE) }
    val loginProvider = remember { sharedPrefs.getString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, null) }
    val isGuest = loginProvider.isNullOrBlank() || loginProvider == AppConstants.Providers.ANONYMOUS
    val coroutineScope = rememberCoroutineScope()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var internalEnabled by remember { mutableStateOf(AppConstants.isInternalEnabled(context)) }
    var versionTapCount by remember { mutableStateOf(0) }
    var tapResetJob by remember { mutableStateOf<Job?>(null) }
    var internalTapCount by remember { mutableStateOf(0) }
    var internalTapResetJob by remember { mutableStateOf<Job?>(null) }
    var isSignOutLoading by remember { mutableStateOf(false) }
    var isResetLoading by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var isDeleteAccountLoading by remember { mutableStateOf(false) }
    var showDeleteGuestDialog by remember { mutableStateOf(false) }
    var confirmAction by remember { mutableStateOf(ConfirmAction.NONE) }

    //To Update the version of  app
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    val versionName = packageInfo.versionName
    val versionCode = packageInfo.longVersionCode

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
            // Ensure any persisted Supabase session blob is wiped
            try { viewModel.clearSupabaseLocalSession() } catch (_: Exception) {}
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
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        SettingHeader(onDismiss = onDismiss)

        Spacer(Modifier.height(20.dp))

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

        Spacer(Modifier.height(20.dp))


        SettingSection(title = "ACCOUNT") {
            if (isGuest) {
                // Guest/anonymous flow: only allow clearing local data and restart
                IconRow(
                    "Reset App State",
                    R.drawable.fluent_warning_20_regular,
                    tint = AppColors.ErrorStrong,
                    tint2 = AppColors.ErrorStrong,
                    showDivider = false,
                    trailingLoading = isResetLoading
                ) { confirmAction = ConfirmAction.RESET_GUEST }
            } else {
                // Authenticated user: show Sign Out and Delete Data & Account
                IconRow(
                    "Sign Out",
                    R.drawable.stash_signout_light__1_,
                    tint = AppColors.Brand,
                    showArrow = false,
                    trailingLoading = isSignOutLoading
                ) {
                    if (!isSignOutLoading) {
                        isSignOutLoading = true
                        clearAllSession()
                    }
                }
                IconRow(
                    "Delete Data & Account",
                    R.drawable.fluent_warning_20_regular,
                    tint = AppColors.ErrorStrong,
                    tint2 = AppColors.ErrorStrong,
                    showDivider = false,
                    trailingLoading = isDeleteAccountLoading
                ) { if (!isDeleteAccountLoading) confirmAction = ConfirmAction.DELETE_ACCOUNT }
            }
        }

        Spacer(Modifier.height(20.dp))

        // 🔹 Section: About
        SettingSection(title = "ABOUT") {
            IconRow(
                "About me",
                R.drawable.healthicons_ui_user_profile,
//                R.drawable.rightbackbutton
            ) { selectedUrl = AppConstants.Website.ABOUT }

//            IconRow(
//                "Tip Jar",
//                Icons.Default.FavoriteBorder,
////                R.drawable.rightbackbutton
//            ) { selectedUrl = "https://www.ingredicheck.app/tipjar" }

            IconRow(
                "Help",
                R.drawable.hugeicons_help_circle,
//                R.drawable.rightbackbutton
            ) { selectedUrl = AppConstants.Website.ABOUT }

            IconRow(
                "Terms of Use",
                R.drawable.iconoir_multiple_pages,
//                R.drawable.rightbackbutton
            ) { selectedUrl = AppConstants.Website.TERMS }

            IconRow(
                "Privacy Policy",
                R.drawable.meteor_icons_lock,
//                R.drawable.rightbackbutton
            ) { selectedUrl = AppConstants.Website.PRIVACY }

            if (internalEnabled) {
                IconRow(
                    "Internal Mode Enabled",
                    R.drawable.fluent_warning_20_regular,
                    tint = AppColors.Brand,
                    tint2 = AppColors.Brand,
                    showDivider = true,
                    showArrow = false,
                    onClick = {
                        internalTapCount += 1
                        if (internalTapCount == 1) {
                            internalTapResetJob?.cancel()
                            internalTapResetJob = coroutineScope.launch {
                                delay(1500)
                                internalTapCount = 0
                            }
                        }
                        if (internalTapCount >= 7) {
                            internalTapCount = 0
                            internalTapResetJob?.cancel()
                            viewModel.disableInternalMode(context)
                            internalEnabled = false
                            Toast.makeText(context, "Internal Mode Disabled", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            IconRow(
                "IngrediCheck for Android $versionName($versionCode)",
                R.drawable.rectangle_34624324__1_,
//                null,
                showDivider = false,
                onClick = {
                    versionTapCount += 1
                    if (versionTapCount == 1) {
                        tapResetJob?.cancel()
                        tapResetJob = coroutineScope.launch {
                            delay(1500)
                            versionTapCount = 0
                        }
                    }
                    if (versionTapCount >= 7) {
                        versionTapCount = 0
                        tapResetJob?.cancel()
                        viewModel.enableInternalMode(context)
                        internalEnabled = true
                        Toast.makeText(context, "Internal Mode Enabled", Toast.LENGTH_SHORT).show()
                    }
                }
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
                        isDeleteAccountLoading = true
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
                        isResetLoading = true
                        runCatching { viewModel.signOut(context) }
                        runCatching { viewModel.clearSupabaseLocalSession() }
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
            delay(100)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        // Centered Text
        Text(
            text = "SETTINGS",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                letterSpacing = (-0.41).sp,
                color = AppColors.Neutral700,
                lineHeight = 22.sp
            )
        )

        // Right-aligned Icon
        Icon(
            painter = painterResource(R.drawable.trailingicon),
            contentDescription = "Dismiss",
            tint = Greyscale400,
            modifier = Modifier
                .align(Alignment.CenterEnd) // stick to right
                .size(22.dp)
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
                fontWeight = FontWeight.W400,
                fontSize = 17.sp,
                letterSpacing = 0.06.em,
                textAlign = TextAlign.Start,
                color = Greyscale400
            ),
            modifier = Modifier.padding(start = 15.dp, bottom = 5.dp)
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
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
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
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
    leadingVector: Int,
    tint: Color = Color.Black, //
    tint2: Color =  AppColors.Brand,
    showDivider: Boolean = true, //
    showArrow: Boolean = true,  // ✅ new flag
    trailingLoading: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .clickable(
                    enabled = !trailingLoading,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick?.invoke() }
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = leadingVector),
                contentDescription = null,
                tint = tint2
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    //   letterSpacing = (-0.41).sp,
                    color = tint
//                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if (trailingLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = AppColors.Brand
                )
            } else if (showDivider && showArrow) {
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
                thickness = 1.dp,
                modifier = Modifier.padding(start = 47.dp)
            )
        }
    }
}

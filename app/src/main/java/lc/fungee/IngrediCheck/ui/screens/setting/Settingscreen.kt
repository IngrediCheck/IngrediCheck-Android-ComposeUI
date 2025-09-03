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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.*

@Composable
fun SettingScreen(onDismiss: () -> Unit = {}) {
    var autoScan by remember { mutableStateOf(false) }
    var selectedUrl by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.94f)
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
                onCheckedChange = { autoScan = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Section: Account
        SettingSection(title = "ACCOUNT") {
            SwitchRow(
                text = "Enable Account Sync",
                checked = autoScan,
                onCheckedChange = { autoScan = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Section: About
        SettingSection(title = "ABOUT") {
            IconRow(
                "About me",
                Icons.Default.AccountCircle,
                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/about" }

            IconRow(
                "Tip Jar",
                Icons.Default.FavoriteBorder,
                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/tipjar" }

            IconRow(
                "Help",
                Icons.Default.Info,
                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/help" }

            IconRow(
                "Terms of Use",
                Icons.Default.AddCircle,
                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/terms" }

            IconRow(
                "Privacy Policy",
                Icons.Default.Lock,
                R.drawable.rightbackbutton
            ) { selectedUrl = "https://www.ingredicheck.app/privacy" }

            IconRow(
                "IngrediCheck for Android 1.0.(38)",
                Icons.Default.Star,
                null,
                showDivider = false
            )
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
                color = Color(0xFF1B270C),
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
                color = Color(0xFF1B270C),
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
                checkedTrackColor = PrimaryGreen100,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFF3F2F9),
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
    trailingIcon: Int?,
    showDivider: Boolean = true, // 👈 optional
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
                tint = PrimaryGreen100
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    //   letterSpacing = (-0.41).sp,
                    color = Color(0xFF1B270C),
//                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            trailingIcon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }

        // 👇 Divider only if needed
        if (showDivider) {
            Divider(
                color = Color(0xFFF3F2F9),
                thickness = 2.dp,
                modifier = Modifier.padding(start = 35.dp)
            )
        }
    }
}

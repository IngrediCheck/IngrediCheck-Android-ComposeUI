package lc.fungee.Ingredicheck.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PaletteAccent,
    onPrimary = PaletteForegroundDark,
    secondary = PaletteSecondary,
    onSecondary = PaletteForegroundDark,
    tertiary = PaletteTertiary,
    onTertiary = PaletteForegroundLight,
    background = PaletteBackgroundDark,
    onBackground = PaletteForegroundDark,
    surface = PaletteBackgroundDark,
    onSurface = PaletteForegroundDark,
    error = Fail100,
    onError = PaletteForegroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = PaletteAccent,
    onPrimary = PaletteBackgroundLight,
    secondary = PaletteSecondary,
    onSecondary = PaletteBackgroundLight,
    tertiary = PaletteTertiary,
    onTertiary = PaletteForegroundLight,
    background = PaletteBackgroundLight,
    onBackground = PaletteForegroundLight,
    surface = PaletteBackgroundLight,
    onSurface = PaletteForegroundLight,
    error = Fail100,
    onError = Color.White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun IngrediCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
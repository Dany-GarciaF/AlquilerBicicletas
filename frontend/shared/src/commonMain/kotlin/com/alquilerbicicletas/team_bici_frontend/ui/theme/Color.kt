package com.alquilerbicicletas.team_bici_frontend.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Brand palette: vivid orange over cream / ink surfaces. */
internal object Palette {
    val Orange = Color(0xFFDE4B12)
    val OrangeBright = Color(0xFFFF7A3D)
    val Amber = Color(0xFFF59E3B)
    val Ink = Color(0xFF14161A)
    val Cream = Color(0xFFF3EFE9)
}

/** Same in light and dark so white text on top keeps its contrast. */
val BrandGradient: Brush = Brush.linearGradient(listOf(Color(0xFFDE4B12), Color(0xFFF07A1A)))

internal val LightColors: ColorScheme = lightColorScheme(
    primary = Palette.Orange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCB),
    onPrimaryContainer = Color(0xFF3B1000),
    secondary = Palette.Ink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAE4DA),
    onSecondaryContainer = Palette.Ink,
    tertiary = Palette.Amber,
    onTertiary = Palette.Ink,
    tertiaryContainer = Color(0xFFFFE6C7),
    onTertiaryContainer = Color(0xFF3A2400),
    background = Palette.Cream,
    onBackground = Palette.Ink,
    surface = Color.White,
    onSurface = Palette.Ink,
    surfaceVariant = Color(0xFFE9E3D9),
    onSurfaceVariant = Color(0xFF55504A),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAF7F2),
    surfaceContainer = Color(0xFFF5F1EA),
    surfaceContainerHigh = Color(0xFFEFEAE1),
    surfaceContainerHighest = Color(0xFFE9E3D9),
    outline = Color(0xFF8C857B),
    outlineVariant = Color(0xFFDDD6CA),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

internal val DarkColors: ColorScheme = darkColorScheme(
    primary = Palette.OrangeBright,
    onPrimary = Color(0xFF2A0D00),
    primaryContainer = Color(0xFF7A2A09),
    onPrimaryContainer = Color(0xFFFFDBCB),
    secondary = Color(0xFFEDE7DD),
    onSecondary = Palette.Ink,
    secondaryContainer = Color(0xFF2B2824),
    onSecondaryContainer = Color(0xFFEDE7DD),
    tertiary = Color(0xFFFFB067),
    onTertiary = Color(0xFF3A2400),
    tertiaryContainer = Color(0xFF5A3A12),
    onTertiaryContainer = Color(0xFFFFE6C7),
    background = Color(0xFF0F0E0D),
    onBackground = Palette.Cream,
    surface = Color(0xFF1A1816),
    onSurface = Palette.Cream,
    surfaceVariant = Color(0xFF2B2824),
    onSurfaceVariant = Color(0xFFB9B2A7),
    surfaceContainerLowest = Color(0xFF141210),
    surfaceContainerLow = Color(0xFF1D1B18),
    surfaceContainer = Color(0xFF211E1B),
    surfaceContainerHigh = Color(0xFF2A2622),
    surfaceContainerHighest = Color(0xFF332F2A),
    outline = Color(0xFF8A8379),
    outlineVariant = Color(0xFF3A3631),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

/** Semantic colors that Material 3 does not define (success / warning states). */
@Immutable
data class ExtendedColors(
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
)

internal val LightExtendedColors = ExtendedColors(
    successContainer = Color(0xFFDCF3E1),
    onSuccessContainer = Color(0xFF14532D),
    warningContainer = Color(0xFFFFEBC2),
    onWarningContainer = Color(0xFF5C3B00),
)

internal val DarkExtendedColors = ExtendedColors(
    successContainer = Color(0xFF16341F),
    onSuccessContainer = Color(0xFF8EE0A4),
    warningContainer = Color(0xFF4A3510),
    onWarningContainer = Color(0xFFFFD98A),
)

internal val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

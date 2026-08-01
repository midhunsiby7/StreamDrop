package com.streamdrop.app.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * StreamDrop Application Theme
 *
 * Wraps MaterialTheme with the custom color schemes, typography, and
 * shape configuration. Exposes [LocalStreamDropColors] for extra design
 * tokens not covered by Material 3 (e.g. glass surfaces, gradient stops).
 */

// ─── Extra design tokens not in M3 ───────────────────────────────────────────

/**
 * Holder for design tokens that are specific to StreamDrop and aren't
 * directly mapped to Material 3 roles.
 */
data class StreamDropColors(
    val glassBackground: Color = SurfaceGlass,
    val borderSubtle: Color = BorderSubtle,
    val borderMedium: Color = BorderMedium,
    val borderGlow: Color = BorderGlow,
    val gradientStart: Color = GradientStartViolet,
    val gradientEnd: Color = GradientEndTeal,
    val statusSuccess: Color = StatusSuccess,
    val statusWarning: Color = StatusWarning,
    val statusError: Color = StatusError,
    val statusInfo: Color = StatusInfo,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textTertiary: Color = TextTertiary,
)

val LocalStreamDropColors = compositionLocalOf { StreamDropColors() }

// ─── Main Theme Composable ────────────────────────────────────────────────────

@Composable
fun StreamDropTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        StreamDropDarkColorScheme
    } else {
        StreamDropLightColorScheme
    }

    val extraColors = if (darkTheme) {
        StreamDropColors()
    } else {
        StreamDropColors(
            glassBackground = Color(0x147C6AF7),
            borderSubtle    = Color(0x1A000000),
            borderMedium    = Color(0x33000000),
            borderGlow      = Color(0x407C6AF7),
            gradientStart   = GradientStartViolet,
            gradientEnd     = GradientEndTeal,
            textPrimary     = Color(0xFF1A1B25),
            textSecondary   = Color(0xFF4A4A6A),
            textTertiary    = Color(0xFF8888AA),
        )
    }

    // Make status bar and nav bar fully transparent so Compose draws edge-to-edge
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalStreamDropColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = StreamDropTypography,
            content     = content,
        )
    }
}

/** Convenience accessor for StreamDrop-specific tokens */
val streamDropColors: StreamDropColors
    @Composable get() = LocalStreamDropColors.current

package com.streamdrop.app.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * StreamDrop Material Design 3 Color Schemes
 *
 * Dark (default): Deep navy-black background with violet-purple primary and teal accent.
 * Light: Clean white/lavender surface with deeper violet primary.
 */

val StreamDropDarkColorScheme = darkColorScheme(
    primary           = Violet500,
    onPrimary         = TextOnPrimary,
    primaryContainer  = Color(0xFF3D2DCC),
    onPrimaryContainer = Color(0xFFE0DBFF),

    secondary         = Teal400,
    onSecondary       = Color(0xFF002020),
    secondaryContainer = Color(0xFF1A4A47),
    onSecondaryContainer = Color(0xFFB2EFEB),

    tertiary          = Coral500,
    onTertiary        = Color(0xFF2B0000),
    tertiaryContainer = Color(0xFF5C1A1A),
    onTertiaryContainer = Color(0xFFFFDAD6),

    background        = Background,
    onBackground      = TextPrimary,

    surface           = SurfaceBase,
    onSurface         = TextPrimary,
    surfaceVariant    = SurfaceElevated,
    onSurfaceVariant  = TextSecondary,

    outline           = BorderSubtle,
    outlineVariant    = BorderMedium,

    error             = Coral500,
    onError           = Color(0xFF2B0000),
    errorContainer    = Color(0xFF5C1A1A),
    onErrorContainer  = Color(0xFFFFDAD6),

    inverseSurface    = TextPrimary,
    inverseOnSurface  = Background,
    inversePrimary    = Violet600,

    surfaceTint       = Violet500,
    scrim             = Color(0xCC000000),
)

val StreamDropLightColorScheme = lightColorScheme(
    primary           = LightPrimary,
    onPrimary         = LightOnPrimary,
    primaryContainer  = Color(0xFFEDE8FF),
    onPrimaryContainer = Color(0xFF3D2DCC),

    secondary         = LightSecondary,
    onSecondary       = LightOnSecondary,
    secondaryContainer = Color(0xFFB2EFEB),
    onSecondaryContainer = Color(0xFF002020),

    tertiary          = Coral600,
    onTertiary        = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDAD6),
    onTertiaryContainer = Color(0xFF5C1A1A),

    background        = LightBackground,
    onBackground      = Color(0xFF1A1B25),

    surface           = LightSurface,
    onSurface         = Color(0xFF1A1B25),
    surfaceVariant    = LightSurfaceVariant,
    onSurfaceVariant  = Color(0xFF4A4A6A),

    outline           = Color(0xFFCCCCDD),
    outlineVariant    = Color(0xFFDDDDEE),

    error             = Coral600,
    onError           = Color(0xFFFFFFFF),
    errorContainer    = Color(0xFFFFDAD6),
    onErrorContainer  = Color(0xFF5C1A1A),
)

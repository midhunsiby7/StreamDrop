package com.streamdrop.app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * StreamDrop Typography
 *
 * Uses the system default sans-serif family (which on most Android devices maps
 * to Roboto or the OEM typeface). Switching to Inter is a one-line change once
 * the font files are added to res/font/ — just replace `FontFamily.SansSerif`
 * with `InterFontFamily`.
 *
 * Scale follows Material Design 3 type roles.
 */

// ── Font family ─────────────────────────────────────────────────────────────
// When Inter font files (inter_regular.ttf, inter_medium.ttf, inter_semibold.ttf,
// inter_bold.ttf, inter_extrabold.ttf) are added to res/font/, replace this with:
//
//   val InterFontFamily = FontFamily(
//       Font(R.font.inter_regular,   FontWeight.Normal),
//       Font(R.font.inter_medium,    FontWeight.Medium),
//       Font(R.font.inter_semibold,  FontWeight.SemiBold),
//       Font(R.font.inter_bold,      FontWeight.Bold),
//       Font(R.font.inter_extrabold, FontWeight.ExtraBold),
//   )
//
val InterFontFamily: FontFamily = FontFamily.SansSerif

val StreamDropTypography = Typography(
    // ─── Display ──────────────────────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 57.sp,
        lineHeight    = 64.sp,
        letterSpacing = (-0.25).sp,
        color         = TextPrimary,
    ),
    displayMedium = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 45.sp,
        lineHeight    = 52.sp,
        letterSpacing = 0.sp,
        color         = TextPrimary,
    ),
    displaySmall = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 36.sp,
        lineHeight    = 44.sp,
        letterSpacing = 0.sp,
        color         = TextPrimary,
    ),

    // ─── Headline ─────────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 32.sp,
        lineHeight    = 40.sp,
        letterSpacing = 0.sp,
        color         = TextPrimary,
    ),
    headlineMedium = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = 0.sp,
        color         = TextPrimary,
    ),
    headlineSmall = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 24.sp,
        lineHeight    = 32.sp,
        letterSpacing = 0.sp,
        color         = TextPrimary,
    ),

    // ─── Title ────────────────────────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 22.sp,
        lineHeight    = 28.sp,
        letterSpacing = 0.sp,
        color         = TextPrimary,
    ),
    titleMedium = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.15.sp,
        color         = TextPrimary,
    ),
    titleSmall = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp,
        color         = TextSecondary,
    ),

    // ─── Body ─────────────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.5.sp,
        color         = TextPrimary,
    ),
    bodyMedium = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.25.sp,
        color         = TextSecondary,
    ),
    bodySmall = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.4.sp,
        color         = TextTertiary,
    ),

    // ─── Label ────────────────────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp,
        color         = TextPrimary,
    ),
    labelMedium = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp,
        color         = TextSecondary,
    ),
    labelSmall = TextStyle(
        fontFamily    = InterFontFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp,
        color         = TextTertiary,
    ),
)

package com.streamdrop.app.core.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Primary Palette ──────────────────────────────────────────────────────────
/** Soft violet-purple — the StreamDrop brand color */
val Violet400 = Color(0xFF9D8FFF)
val Violet500 = Color(0xFF7C6AF7)
val Violet600 = Color(0xFF5B48E8)
val Violet700 = Color(0xFF3D2DCC)

// ─── Secondary / Accent ───────────────────────────────────────────────────────
/** Teal accent — used for active progress, success states, CTAs */
val Teal300 = Color(0xFF72DDD7)
val Teal400 = Color(0xFF4ECDC4)
val Teal500 = Color(0xFF2EBFB7)

// ─── Tertiary / Warning ───────────────────────────────────────────────────────
/** Warm coral — used for errors, cancel actions */
val Coral400 = Color(0xFFFF8A80)
val Coral500 = Color(0xFFFF6B6B)
val Coral600 = Color(0xFFE55555)

// ─── Background Scale ─────────────────────────────────────────────────────────
/** True dark base — almost black with a navy tint */
val Background = Color(0xFF0D0E14)
/** Cards and surfaces sit above the background */
val SurfaceBase = Color(0xFF1A1B25)
/** Elevated cards (e.g. bottom sheet) */
val SurfaceElevated = Color(0xFF22243A)
/** Glass overlay tint on top of surfaces */
val SurfaceGlass = Color(0x147C6AF7)

// ─── Border & Dividers ────────────────────────────────────────────────────────
val BorderSubtle = Color(0x1AFFFFFF)   // 10% white
val BorderMedium = Color(0x33FFFFFF)   // 20% white
val BorderGlow  = Color(0x407C6AF7)   // 25% violet

// ─── Text Scale ───────────────────────────────────────────────────────────────
val TextPrimary   = Color(0xFFF2F3FF)   // Near-white with violet tint
val TextSecondary = Color(0xFFB4B8D8)   // Muted lavender
val TextTertiary  = Color(0xFF6B7096)   // Disabled / placeholder
val TextOnPrimary = Color(0xFFFFFFFF)

// ─── Gradient Definitions (for Brush usage) ───────────────────────────────────
/** Use these with Brush.linearGradient() or Brush.verticalGradient() */
val GradientStartViolet = Violet500
val GradientEndTeal     = Teal400

// ─── Status Colors ────────────────────────────────────────────────────────────
val StatusSuccess = Color(0xFF4CAF50)
val StatusWarning = Color(0xFFFFC107)
val StatusError   = Coral500
val StatusInfo    = Violet400

// ─── Light Theme (minimal — dark is default) ──────────────────────────────────
val LightBackground    = Color(0xFFF5F5FF)
val LightSurface       = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEDE8FF)
val LightPrimary       = Violet600
val LightOnPrimary     = Color(0xFFFFFFFF)
val LightSecondary     = Teal500
val LightOnSecondary   = Color(0xFF002020)

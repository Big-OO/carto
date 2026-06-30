package com.example.carto.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
//  Ocean Deep — Color Palette
// ─────────────────────────────────────────────

// Primary — Deep Navy Blue
val NavyDeep        = Color(0xFF0D2B6E)   // darkest navy
val NavyPrimary     = Color(0xFF0D47A1)   // primary brand blue
val NavyMedium      = Color(0xFF1565C0)   // lighter navy
val NavyLight       = Color(0xFF1976D2)   // for gradients

// Accent — Vivid Cyan / Teal
val CyanAccent      = Color(0xFF00BCD4)   // main accent
val CyanLight       = Color(0xFF4DD0E1)   // gradient end / highlight
val CyanSoft        = Color(0xFFB2EBF2)   // very soft cyan tint

// Backgrounds
val BackgroundBlue  = Color(0xFFF0F4FF)   // blue-tinted white
val BackgroundDeep  = Color(0xFFE8EEF9)   // slightly deeper
val SurfaceWhite    = Color(0xFFFFFFFF)   // cards / surfaces

// Text
val TextDeep        = Color(0xFF0A1628)   // near-black navy
val TextMedium      = Color(0xFF2C3E6B)   // mid-tone navy text
val TextMuted       = Color(0xFF546E7A)   // blue-grey sub-text
val TextHint        = Color(0xFF90A4AE)   // placeholder / hint

// Borders & Dividers
val BorderDefault   = Color(0xFFB0BEC5)   // cool grey idle border
val BorderFocus     = Color(0xFF1565C0)   // focus ring
val DividerLine     = Color(0xFFDDE4F0)   // divider line

// Semantic
val SemanticSuccess = Color(0xFF00897B)   // teal-green — valid
val SemanticError   = Color(0xFFE53935)   // deep red — error
val SemanticWarning = Color(0xFFFB8C00)   // amber — warning

// Bottom Nav
val NavBarBg        = Color(0xFF0D47A1)   // navy pill background
val NavBarSelected  = Color(0xFF00BCD4)   // cyan selected item
val NavBarUnselected= Color(0xFFB0C8F5)   // muted blue unselected

// Material3 ColorScheme mappings (light)
val Primary80       = NavyLight
val PrimaryGrey80   = Color(0xFF90A4AE)
val CyanAccent80    = CyanLight

val Primary40       = NavyPrimary
val PrimaryGrey40   = Color(0xFF546E7A)
val CyanAccent40    = CyanAccent
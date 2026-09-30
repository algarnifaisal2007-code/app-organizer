package com.faisal.apporganizer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val OrganizerColors = darkColorScheme(
    primary = Color(0xFF9D8CFF),
    onPrimary = Color(0xFF1A1340),
    secondary = Color(0xFF5FD4C8),
    onSecondary = Color(0xFF00201D),
    background = Color(0xFF100F22),
    onBackground = Color(0xFFEDEBFF),
    surface = Color(0xFF1A1933),
    onSurface = Color(0xFFEDEBFF),
    surfaceVariant = Color(0xFF26254A),
    onSurfaceVariant = Color(0xFFB9B6DA),
    surfaceContainerLow = Color(0xFF1A1933),
    surfaceContainer = Color(0xFF1E1D3A),
    surfaceContainerHigh = Color(0xFF26254A),
    outline = Color(0xFF4A4878),
)

val HeroGradient = Brush.linearGradient(listOf(Color(0xFF6A5AE0), Color(0xFF3FB6C9)))
val ScreenGradient = Brush.verticalGradient(listOf(Color(0xFF17153A), Color(0xFF0C0B1A)))
val FavoriteGold = Color(0xFFFFC857)

/** سمة داكنة مع اتجاه من اليمين إلى اليسار في كل الواجهة. */
@Composable
fun OrganizerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = OrganizerColors) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    }
}

package io.skrastrek.snake.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Provides Neo-Retro Arcade glass/glow tokens that have no Material 3 slot.
 * Access via [AppTheme.glass].
 */
private val LocalGlassColors = staticCompositionLocalOf { GlassColors() }

/** Material 3 [Shapes] mapped from [AppDimensions] corner radii. */
private val SnakeShapes = Shapes(
    extraSmall = RoundedCornerShape(AppDimensions.RadiusSm),
    small = AppDimensions.ShapeCard,
    medium = RoundedCornerShape(AppDimensions.RadiusMd),
    large = AppDimensions.ShapeOverlay,
    extraLarge = RoundedCornerShape(AppDimensions.RadiusXl),
)

/**
 * Root theme for the Snake app. Wraps Material 3 with the Neo-Retro Arcade
 * color scheme, typography and shapes, and exposes extra glass tokens via
 * [AppTheme].
 */
@Composable
fun SnakeTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalGlassColors provides GlassColors()) {
        MaterialTheme(
            colorScheme = SnakeColorScheme,
            typography = SnakeTypography,
            shapes = SnakeShapes,
            content = content,
        )
    }
}

/** Convenience accessor for design tokens not covered by [MaterialTheme]. */
object AppTheme {
    /** Glassmorphism + glow tokens. */
    val glass: GlassColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGlassColors.current
}

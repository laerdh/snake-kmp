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

/**
 * Provides the brand typographic tokens. Defaults to the system-font
 * [FallbackTypography]; [SnakeTheme] overrides it with the bundled fonts.
 * Access via [AppTheme.type].
 */
private val LocalAppTypography = staticCompositionLocalOf { FallbackTypography }

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
 * color scheme, the bundled Sora / JetBrains Mono typography and shapes, and
 * exposes extra glass + typography tokens via [AppTheme].
 */
@Composable
fun SnakeTheme(content: @Composable () -> Unit) {
    val typography = rememberBrandTypography()
    CompositionLocalProvider(
        LocalGlassColors provides GlassColors(),
        LocalAppTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = SnakeColorScheme,
            typography = materialTypography(typography),
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

    /** Brand typographic tokens (Sora / JetBrains Mono). */
    val type: AppTypographyTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current
}

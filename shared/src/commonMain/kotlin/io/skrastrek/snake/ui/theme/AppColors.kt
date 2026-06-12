package io.skrastrek.snake.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Color tokens for the "Neo-Retro Arcade" design system.
 *
 * Source of truth: `DESIGN.md`. Every value here maps directly to a token in that
 * file — do not introduce inline `Color(0xFF…)` literals elsewhere in the codebase.
 */
object AppColors {
    // Surfaces & background
    val Background = Color(0xFF0B1326)
    val Surface = Color(0xFF0B1326)
    val SurfaceBright = Color(0xFF31394D)
    val SurfaceContainerLowest = Color(0xFF060E20)
    val SurfaceContainerLow = Color(0xFF131B2E)
    val SurfaceContainer = Color(0xFF171F33)
    val SurfaceContainerHigh = Color(0xFF222A3D)
    val SurfaceContainerHighest = Color(0xFF2D3449)
    val SurfaceVariant = Color(0xFF2D3449)

    // Content
    val OnSurface = Color(0xFFDAE2FD)
    val OnSurfaceVariant = Color(0xFFBCCBB9)
    val OnBackground = Color(0xFFDAE2FD)
    val Outline = Color(0xFF869585)
    val OutlineVariant = Color(0xFF3D4A3D)

    // Primary — Electric Lime (the snake, success states)
    val Primary = Color(0xFF4BE277)
    val OnPrimary = Color(0xFF003915)
    val PrimaryContainer = Color(0xFF22C55E)
    val OnPrimaryContainer = Color(0xFF004B1E)
    val SurfaceTint = Color(0xFF4AE176)

    // Secondary — Hot Pink (food, high-score alerts)
    val Secondary = Color(0xFFFFB0CD)
    val OnSecondary = Color(0xFF640039)
    val SecondaryContainer = Color(0xFFAA0266)
    val OnSecondaryContainer = Color(0xFFFFBAD3)

    /** Saturated neon magenta for the Game Over glitch title and "new best" badge. */
    val NeonPink = Color(0xFFFF00CD)

    // Tertiary — Cyan (functional accents, borders, power-ups)
    val Tertiary = Color(0xFF4DD8F7)
    val OnTertiary = Color(0xFF003640)
    val TertiaryContainer = Color(0xFF1DBCDA)
    val OnTertiaryContainer = Color(0xFF004754)

    // Error
    val Error = Color(0xFFFFB4AB)
    val OnError = Color(0xFF690005)
    val ErrorContainer = Color(0xFF93000A)
    val OnErrorContainer = Color(0xFFFFDAD6)

    // Inverse
    val InverseSurface = Color(0xFFDAE2FD)
    val InverseOnSurface = Color(0xFF283044)
    val InversePrimary = Color(0xFF006E2F)
}

/**
 * Extra design tokens that have no Material 3 [ColorScheme] slot but are part of the
 * Neo-Retro Arcade language (glassmorphism overlays, luminescent borders).
 */
@Immutable
data class GlassColors(
    /** Semi-transparent fill for Layer 1 cards/overlays (70% opacity surface). */
    val glassSurface: Color = AppColors.SurfaceContainer.copy(alpha = 0.70f),
    /** Thin 30%-opacity cyan stroke used on glass cards. */
    val glassBorder: Color = AppColors.Tertiary.copy(alpha = 0.30f),
    /** Outer glow tint for Layer 2 modals and active (bloom) buttons. */
    val glow: Color = AppColors.Primary,
)

/** Material 3 dark color scheme assembled from [AppColors]. */
internal val SnakeColorScheme: ColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimaryContainer = AppColors.OnPrimaryContainer,
    inversePrimary = AppColors.InversePrimary,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryContainer,
    onSecondaryContainer = AppColors.OnSecondaryContainer,
    tertiary = AppColors.Tertiary,
    onTertiary = AppColors.OnTertiary,
    tertiaryContainer = AppColors.TertiaryContainer,
    onTertiaryContainer = AppColors.OnTertiaryContainer,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    surfaceTint = AppColors.SurfaceTint,
    surfaceBright = AppColors.SurfaceBright,
    surfaceContainerLowest = AppColors.SurfaceContainerLowest,
    surfaceContainerLow = AppColors.SurfaceContainerLow,
    surfaceContainer = AppColors.SurfaceContainer,
    surfaceContainerHigh = AppColors.SurfaceContainerHigh,
    surfaceContainerHighest = AppColors.SurfaceContainerHighest,
    outline = AppColors.Outline,
    outlineVariant = AppColors.OutlineVariant,
    error = AppColors.Error,
    onError = AppColors.OnError,
    errorContainer = AppColors.ErrorContainer,
    onErrorContainer = AppColors.OnErrorContainer,
    inverseSurface = AppColors.InverseSurface,
    inverseOnSurface = AppColors.InverseOnSurface,
)

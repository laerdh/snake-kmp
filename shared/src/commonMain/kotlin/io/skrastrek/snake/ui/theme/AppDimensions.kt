package io.skrastrek.snake.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing, sizing and corner-radius tokens for the Neo-Retro Arcade design system.
 *
 * Source of truth: `DESIGN.md`. The spacing scale is built on a 4px baseline grid;
 * never hardcode `.dp` literals in UI code — reference these tokens instead.
 */
@Immutable
object AppDimensions {
    // Spacing scale (4px baseline grid)
    val SpacingXs: Dp = 4.dp
    val SpacingSm: Dp = 8.dp
    val SpacingMd: Dp = 16.dp
    val SpacingLg: Dp = 24.dp
    val SpacingXl: Dp = 32.dp

    /** Minimum interactive touch target per the design spec. */
    val TouchTarget: Dp = 48.dp

    /** Default horizontal padding for screen containers. */
    val ContainerPadding: Dp = 20.dp

    // Corner radii
    val RadiusSm: Dp = 4.dp
    val RadiusDefault: Dp = 8.dp
    val RadiusMd: Dp = 12.dp
    val RadiusLg: Dp = 16.dp
    val RadiusXl: Dp = 24.dp

    // Component-specific shapes
    /** Cards & menu buttons. */
    val ShapeCard = RoundedCornerShape(RadiusDefault)
    /** Snake segments — smooth, organic feel. */
    val ShapeSnakeSegment = RoundedCornerShape(RadiusLg)
    /** Glass overlays / modals. */
    val ShapeOverlay = RoundedCornerShape(RadiusLg)

    // Borders & glow
    val GlassBorderWidth: Dp = 1.dp
    val SecondaryButtonBorderWidth: Dp = 2.dp
    val GlowRadius: Dp = 8.dp
}

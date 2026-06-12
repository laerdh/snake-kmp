package io.skrastrek.snake.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import io.skrastrek.snake.ui.theme.AppColors
import io.skrastrek.snake.ui.theme.AppDimensions
import io.skrastrek.snake.ui.theme.AppTheme

/**
 * Shared building blocks of the Neo-Retro Arcade UI — glass panels, neon buttons,
 * the brand wordmark and the ambient background. Every screen composes these so the
 * glassmorphism + luminescent-border language stays consistent and tokenised.
 */

/** Returns a copy of this style with a soft neon [color] glow (blurred text shadow). */
fun TextStyle.withGlow(color: Color, blurRadius: Float = 24f): TextStyle =
    copy(shadow = Shadow(color = color, offset = Offset.Zero, blurRadius = blurRadius))

/**
 * Full-screen ambient backdrop: the deep midnight base, a faint primary grid and
 * two soft corner glow blobs (electric-lime and cyan). [content] is laid over it.
 */
@Composable
fun NeonBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val gridColor = AppColors.Primary.copy(alpha = 0.05f)
    val limeGlow = AppColors.Primary.copy(alpha = 0.10f)
    val cyanGlow = AppColors.Tertiary.copy(alpha = 0.10f)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .drawBehind {
                val step = AppDimensions.BackgroundGridSpacing.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += step
                }
                // Soft corner glows via radial gradients (no platform blur needed).
                val blob = AppDimensions.GlowBlobSize.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(limeGlow, Color.Transparent),
                        center = Offset(0f, size.height * 0.28f),
                        radius = blob,
                    ),
                    radius = blob,
                    center = Offset(0f, size.height * 0.28f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(cyanGlow, Color.Transparent),
                        center = Offset(size.width, size.height * 0.72f),
                        radius = blob,
                    ),
                    radius = blob,
                    center = Offset(size.width, size.height * 0.72f),
                )
            },
    ) {
        content()
    }
}

/** The "SNAKE.NEO" wordmark in electric lime with a neon glow. */
@Composable
fun BrandWordmark(
    modifier: Modifier = Modifier,
    style: TextStyle = AppTheme.type.headlineLargeMobile,
) {
    Text(
        text = "SNAKE.NEO",
        style = style.copy(fontWeight = FontWeight.Black).withGlow(AppColors.Primary.copy(alpha = 0.6f)),
        color = AppColors.Primary,
        modifier = modifier,
    )
}

/**
 * Glassmorphism container: translucent fill + thin cyan stroke, rounded corners.
 * The base of every card, panel and overlay in the app.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(AppDimensions.ShapeOverlay)
            .background(AppColors.SurfaceContainer.copy(alpha = 0.70f))
            .border(
                width = AppDimensions.GlassBorderWidth,
                color = AppTheme.glass.glassBorder,
                shape = AppDimensions.ShapeOverlay,
            ),
    ) {
        content()
    }
}

/** Solid electric-lime primary action button with black text and a soft bloom. */
@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    val glow = AppColors.Primary.copy(alpha = 0.35f)
    Row(
        modifier = modifier
            .height(AppDimensions.TouchTarget)
            .drawBehind {
                // Bloom halo behind the button.
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow, Color.Transparent),
                        radius = size.maxDimension * 0.75f,
                    ),
                    radius = size.maxDimension * 0.75f,
                )
            }
            .clip(AppDimensions.ShapeCard)
            .background(AppColors.Primary)
            .clickable(onClick = onClick)
            .padding(horizontal = AppDimensions.SpacingXl),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = AppColors.OnPrimary,
                modifier = Modifier.size(AppDimensions.IconSize).padding(end = AppDimensions.SpacingSm),
            )
        }
        Text(
            text = label,
            style = AppTheme.type.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = AppColors.OnPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Transparent, bordered secondary button. Defaults to a cyan outline; pass
 * [contentColor]/[borderColor] for variants (e.g. the error-red RESET PROGRESS).
 */
@Composable
fun SecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    contentColor: Color = AppColors.OnSurfaceVariant,
    borderColor: Color = AppColors.OnSurfaceVariant.copy(alpha = 0.3f),
) {
    Row(
        modifier = modifier
            .height(AppDimensions.TouchTarget)
            .clip(AppDimensions.ShapeCard)
            .border(AppDimensions.SecondaryButtonBorderWidth, borderColor, AppDimensions.ShapeCard)
            .clickable(onClick = onClick)
            .padding(horizontal = AppDimensions.SpacingLg),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(AppDimensions.IconSize).padding(end = AppDimensions.SpacingSm),
            )
        }
        Text(
            text = label,
            style = AppTheme.type.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}

/** Circular, touch-target-sized icon button used in top bars and the d-pad. */
@Composable
fun NeonIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = AppColors.OnSurfaceVariant,
    iconSize: Dp = AppDimensions.IconSizeLarge,
) {
    Box(
        modifier = modifier
            .size(AppDimensions.TouchTarget)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}

/**
 * Sticky top navigation bar: optional [leading] control, the brand wordmark, and
 * optional [trailing] controls — with the faint primary underglow from the design.
 */
@Composable
fun SnakeTopBar(
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.Background.copy(alpha = 0.7f))
            .drawBehind {
                drawLine(
                    color = AppColors.Primary.copy(alpha = 0.2f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f,
                )
            }
            .padding(horizontal = AppDimensions.SpacingLg, vertical = AppDimensions.SpacingSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                leading()
            } else {
                GridGlyph(tint = AppColors.Primary)
            }
            BrandWordmark(modifier = Modifier.padding(start = AppDimensions.SpacingSm))
        }
        if (trailing != null) {
            Row(verticalAlignment = Alignment.CenterVertically, content = trailing)
        }
    }
}

/** The 2×2 grid brand glyph (the design's `grid_view` mark), drawn as squares. */
@Composable
fun GridGlyph(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(AppDimensions.IconSizeLarge)
            .drawBehind {
                val gap = size.width * 0.18f
                val cell = (size.width - gap) / 2f
                val offsets = listOf(
                    Offset(0f, 0f),
                    Offset(cell + gap, 0f),
                    Offset(0f, cell + gap),
                    Offset(cell + gap, cell + gap),
                )
                offsets.forEach { o ->
                    drawRoundRect(
                        color = tint,
                        topLeft = o,
                        size = Size(cell, cell),
                        cornerRadius = CornerRadius(cell * 0.2f),
                    )
                }
            },
    )
}

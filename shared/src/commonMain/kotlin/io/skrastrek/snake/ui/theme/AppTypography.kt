package io.skrastrek.snake.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Typographic tokens for the Neo-Retro Arcade design system.
 *
 * Source of truth: `DESIGN.md`. A dual-font system is used:
 *  - **Sora** for headings and general UI text (geometric, bold).
 *  - **JetBrains Mono** for technical data — scores, timers, coordinates.
 *
 * NOTE: The brand fonts (Sora, JetBrains Mono) are not yet bundled as Compose
 * resources, so [Display]/[Body] fall back to [FontFamily.SansSerif] and
 * [ScoreDisplay]/[LabelSmall] to [FontFamily.Monospace]. To ship the real
 * fonts, add the font files under `commonMain/composeResources/font/` and swap
 * the families here — no call-site changes required.
 */
@Immutable
object AppTypography {
    private val Sora = FontFamily.SansSerif
    private val JetBrainsMono = FontFamily.Monospace

    /** display-lg — hero headline (e.g. "SNAKE"). */
    val DisplayLarge = TextStyle(
        fontFamily = Sora,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 52.8.sp, // 1.1
        letterSpacing = (-0.02).em,
    )

    /** headline-lg — section headings (desktop). */
    val HeadlineLarge = TextStyle(
        fontFamily = Sora,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.4.sp, // 1.2
    )

    /** headline-lg-mobile — section headings (mobile). */
    val HeadlineLargeMobile = TextStyle(
        fontFamily = Sora,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 33.6.sp, // 1.2
    )

    /** score-display — large monospaced numerals for the scoreboard. */
    val ScoreDisplay = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 24.sp, // 1.0
        letterSpacing = 0.1.em,
    )

    /** body-md — general body copy. */
    val BodyMedium = TextStyle(
        fontFamily = Sora,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp, // 1.5
    )

    /** label-sm — small monospaced labels (difficulty chips, version). */
    val LabelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 12.sp, // 1.0
    )
}

/** Material 3 [Typography] assembled from [AppTypography] tokens. */
internal val SnakeTypography: Typography = Typography(
    displayLarge = AppTypography.DisplayLarge,
    headlineLarge = AppTypography.HeadlineLarge,
    headlineMedium = AppTypography.HeadlineLargeMobile,
    titleLarge = AppTypography.ScoreDisplay,
    bodyMedium = AppTypography.BodyMedium,
    labelSmall = AppTypography.LabelSmall,
)

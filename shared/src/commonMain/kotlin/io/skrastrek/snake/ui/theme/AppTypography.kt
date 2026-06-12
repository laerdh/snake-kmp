package io.skrastrek.snake.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.skrastrek.snake.shared.resources.Res
import io.skrastrek.snake.shared.resources.jetbrains_mono_bold
import io.skrastrek.snake.shared.resources.jetbrains_mono_medium
import io.skrastrek.snake.shared.resources.sora_bold
import io.skrastrek.snake.shared.resources.sora_extrabold
import io.skrastrek.snake.shared.resources.sora_medium
import io.skrastrek.snake.shared.resources.sora_regular
import org.jetbrains.compose.resources.Font

/**
 * The named typographic tokens of the Neo-Retro Arcade design system.
 *
 * Source of truth: `DESIGN.md`. A dual-font system is used:
 *  - **Sora** for headings and general UI text (geometric, bold).
 *  - **JetBrains Mono** for technical data — scores, timers, coordinates.
 *
 * Held as an [Immutable] bag rather than a top-level `object` because the brand
 * fonts are loaded as Compose resources (`@Composable Font(...)`), so the families
 * can only be resolved during composition. Access via [AppTheme.type].
 *
 * @property displayLarge hero headline (e.g. "SNAKE.NEO").
 * @property headlineLarge section headings (large).
 * @property headlineLargeMobile section headings (mobile).
 * @property scoreDisplay large monospaced numerals for scoreboards.
 * @property bodyMedium general body copy.
 * @property labelSmall small monospaced labels (chips, version).
 */
@Immutable
data class AppTypographyTokens(
    val displayLarge: TextStyle,
    val headlineLarge: TextStyle,
    val headlineLargeMobile: TextStyle,
    val scoreDisplay: TextStyle,
    val bodyMedium: TextStyle,
    val labelSmall: TextStyle,
)

/**
 * Builds the [AppTypographyTokens] from the given [sora] and [mono] families.
 * Shared by both the bundled-font path and the system-fallback default so the
 * sizes/weights stay defined in exactly one place.
 */
private fun typographyTokens(sora: FontFamily, mono: FontFamily): AppTypographyTokens =
    AppTypographyTokens(
        // display-lg
        displayLarge = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 48.sp,
            lineHeight = 52.8.sp, // 1.1
            letterSpacing = (-0.02).em,
        ),
        // headline-lg
        headlineLarge = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.4.sp, // 1.2
        ),
        // headline-lg-mobile
        headlineLargeMobile = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 33.6.sp, // 1.2
        ),
        // score-display
        scoreDisplay = TextStyle(
            fontFamily = mono,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 24.sp, // 1.0
            letterSpacing = 0.1.em,
        ),
        // body-md
        bodyMedium = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp, // 1.5
        ),
        // label-sm
        labelSmall = TextStyle(
            fontFamily = mono,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 12.sp, // 1.0
        ),
    )

/**
 * System-font fallback used as the [LocalAppTypography] default — for previews or
 * any composition that forgets to wrap in [SnakeTheme]. [SnakeTheme] overrides this
 * with the bundled Sora / JetBrains Mono families.
 */
internal val FallbackTypography: AppTypographyTokens =
    typographyTokens(sora = FontFamily.SansSerif, mono = FontFamily.Monospace)

/**
 * Builds the brand [AppTypographyTokens] from the bundled font resources. Must be
 * called inside composition (the resource [Font] loader is `@Composable`).
 */
@Composable
internal fun rememberBrandTypography(): AppTypographyTokens {
    val sora = FontFamily(
        Font(Res.font.sora_regular, FontWeight.Normal),
        Font(Res.font.sora_medium, FontWeight.Medium),
        Font(Res.font.sora_bold, FontWeight.Bold),
        Font(Res.font.sora_extrabold, FontWeight.ExtraBold),
        // Sora's weight axis tops out at ExtraBold (800); reuse it for Black so
        // `FontWeight.Black` call sites still resolve to the heaviest cut.
        Font(Res.font.sora_extrabold, FontWeight.Black),
    )
    val mono = FontFamily(
        Font(Res.font.jetbrains_mono_medium, FontWeight.Medium),
        Font(Res.font.jetbrains_mono_bold, FontWeight.Bold),
    )
    return remember(sora, mono) { typographyTokens(sora, mono) }
}

/** Material 3 [Typography] assembled from the brand [tokens]. */
internal fun materialTypography(tokens: AppTypographyTokens): Typography = Typography(
    displayLarge = tokens.displayLarge,
    headlineLarge = tokens.headlineLarge,
    headlineMedium = tokens.headlineLargeMobile,
    titleLarge = tokens.scoreDisplay,
    bodyMedium = tokens.bodyMedium,
    labelSmall = tokens.labelSmall,
)

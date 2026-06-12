package io.skrastrek.snake.ui.gameover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.skrastrek.snake.ui.components.BrandWordmark
import io.skrastrek.snake.ui.components.GlassCard
import io.skrastrek.snake.ui.components.GridGlyph
import io.skrastrek.snake.ui.components.NeonBackground
import io.skrastrek.snake.ui.components.NeonIconButton
import io.skrastrek.snake.ui.components.PrimaryButton
import io.skrastrek.snake.ui.components.SecondaryButton
import io.skrastrek.snake.ui.components.withGlow
import io.skrastrek.snake.ui.format.toGroupedScore
import io.skrastrek.snake.ui.format.toSurvivalTime
import io.skrastrek.snake.ui.theme.AppColors
import io.skrastrek.snake.ui.theme.AppDimensions
import io.skrastrek.snake.ui.theme.AppTheme

/** Headline TOTAL SCORE size — the design's one-off 64px score numeral. */
private val TotalScoreFontSize = 64.sp

/** Stateless Circuit UI for [GameOverScreen]. */
@Composable
fun GameOverUi(state: GameOverUiState, modifier: Modifier = Modifier) {
    val eventSink = state.eventSink
    NeonBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Background.copy(alpha = 0.7f))
                    .padding(horizontal = AppDimensions.SpacingLg, vertical = AppDimensions.SpacingSm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GridGlyph(tint = AppColors.Primary)
                    BrandWordmark(modifier = Modifier.padding(start = AppDimensions.SpacingSm))
                }
                NeonIconButton(
                    icon = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    onClick = { eventSink(GameOverUiEvent.SettingsClicked) },
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppDimensions.ContainerPadding, vertical = AppDimensions.SpacingXl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingLg),
            ) {
                Spacer(Modifier.height(AppDimensions.SpacingMd))
                GameOverTitle(isNewBest = state.isNewBest)
                ScoreCard(
                    points = state.points,
                    applesEaten = state.applesEaten,
                    elapsedMillis = state.elapsedMillis,
                )
                Spacer(Modifier.height(AppDimensions.SpacingSm))
                PrimaryButton(
                    label = "PLAY AGAIN",
                    onClick = { eventSink(GameOverUiEvent.PlayAgainClicked) },
                    leadingIcon = Icons.Filled.PlayArrow,
                    modifier = Modifier.fillMaxWidth().widthIn(max = AppDimensions.ActionButtonMaxWidth),
                )
                SecondaryButton(
                    label = "HOME",
                    onClick = { eventSink(GameOverUiEvent.HomeClicked) },
                    leadingIcon = Icons.Filled.Home,
                    modifier = Modifier.fillMaxWidth().widthIn(max = AppDimensions.ActionButtonMaxWidth),
                )
            }
        }
    }
}

/** Glitch "GAME OVER" headline + optional "NEW BEST SCORE!" badge. */
@Composable
private fun GameOverTitle(isNewBest: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm),
    ) {
        Text(
            text = "GAME OVER",
            style = AppTheme.type.displayLarge
                .copy(fontStyle = FontStyle.Italic, letterSpacing = 0.1.em)
                .withGlow(AppColors.NeonPink, blurRadius = 24f),
            color = AppColors.NeonPink,
            textAlign = TextAlign.Center,
        )
        if (isNewBest) {
            Row(
                modifier = Modifier
                    .background(AppColors.NeonPink, CircleShape)
                    .padding(horizontal = AppDimensions.SpacingMd, vertical = AppDimensions.SpacingXs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingXs),
            ) {
                Icon(
                    imageVector = Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(AppDimensions.SpacingMd),
                )
                Text(
                    text = "NEW BEST SCORE!",
                    style = AppTheme.type.labelSmall,
                    color = Color.White,
                )
            }
        }
    }
}

/** Central glass card: TOTAL SCORE + the apples/time breakdown. */
@Composable
private fun ScoreCard(
    points: Int,
    applesEaten: Int,
    elapsedMillis: Long,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier.fillMaxWidth().widthIn(max = AppDimensions.ActionButtonMaxWidth)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AppDimensions.SpacingXl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingMd),
        ) {
            Text(
                text = "TOTAL SCORE",
                style = AppTheme.type.labelSmall.copy(letterSpacing = 0.2.em),
                color = AppColors.OnSurfaceVariant,
            )
            Text(
                text = points.toGroupedScore(),
                style = AppTheme.type.scoreDisplay
                    .copy(fontSize = TotalScoreFontSize)
                    .withGlow(AppColors.Primary.copy(alpha = 0.4f), blurRadius = 24f),
                color = AppColors.Primary,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.Hairline)
                    .background(AppColors.Tertiary.copy(alpha = 0.2f)),
            )
            StatRow(
                label = "APPLES EATEN",
                value = applesEaten.toString(),
                icon = Icons.Filled.Restaurant,
                color = AppColors.Secondary,
            )
            StatRow(
                label = "TIME SURVIVED",
                value = elapsedMillis.toSurvivalTime(),
                icon = Icons.Filled.Timer,
                color = AppColors.Tertiary,
            )
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingXs),
    ) {
        Text(
            text = label,
            style = AppTheme.type.labelSmall,
            color = AppColors.OnSurfaceVariant.copy(alpha = 0.5f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(AppDimensions.IconSize),
            )
            Text(
                text = value,
                style = AppTheme.type.scoreDisplay,
                color = color,
            )
        }
    }
}

package io.skrastrek.snake.ui.start

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import io.skrastrek.snake.ui.components.BrandWordmark
import io.skrastrek.snake.ui.components.GlassCard
import io.skrastrek.snake.ui.components.GridGlyph
import io.skrastrek.snake.ui.components.NeonBackground
import io.skrastrek.snake.ui.components.withGlow
import io.skrastrek.snake.ui.format.toGroupedScore
import io.skrastrek.snake.ui.theme.AppColors
import io.skrastrek.snake.ui.theme.AppDimensions
import io.skrastrek.snake.ui.theme.AppTheme
import io.skrastrek.snake.domain.ScoreRepository

/** App version stamped in the footer, matching the design. */
private const val APP_VERSION = "V 2.0.4-NEO"

/** Stateless Circuit UI for [StartScreen]. */
@Composable
fun StartUi(state: StartUiState, modifier: Modifier = Modifier) {
    val eventSink = state.eventSink
    NeonBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppDimensions.ContainerPadding, vertical = AppDimensions.SpacingLg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header brand row.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GridGlyph(tint = AppColors.Primary)
                BrandWordmark(modifier = Modifier.padding(start = AppDimensions.SpacingSm))
            }

            // Hero — the whole block is the "start game" target.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppDimensions.ShapeOverlay)
                    .clickable { eventSink(StartUiEvent.PlayClicked) }
                    .padding(vertical = AppDimensions.SpacingXl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                SnakeMotifLogo()
                Spacer(Modifier.height(AppDimensions.SpacingXl))
                Text(
                    text = "SNAKE.NEO",
                    style = AppTheme.type.displayLarge.withGlow(AppColors.Primary.copy(alpha = 0.8f), blurRadius = 32f),
                    color = AppColors.Primary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(AppDimensions.SpacingXs))
                Text(
                    text = "RETRO CORE // FUTURE TECH",
                    style = AppTheme.type.labelSmall.copy(letterSpacing = 0.3.em),
                    color = AppColors.Tertiary,
                )
                Spacer(Modifier.height(AppDimensions.SpacingXl))
                PlayNowPill()
            }

            Spacer(Modifier.height(AppDimensions.SpacingLg))
            RecentScoresCard(scores = state.recentScores)
            Spacer(Modifier.height(AppDimensions.SpacingLg))
            SystemConfigurationButton(onClick = { eventSink(StartUiEvent.SettingsClicked) })
            Spacer(Modifier.height(AppDimensions.SpacingLg))
            Text(
                text = APP_VERSION,
                style = AppTheme.type.labelSmall,
                color = AppColors.OnSurfaceVariant.copy(alpha = 0.4f),
            )
        }
    }
}

/** The animated-style snake motif: a bordered arena with an L of fading lime cells. */
@Composable
private fun SnakeMotifLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(AppDimensions.LogoSize)) {
        val unit = size.minDimension / 4f
        val cell = unit * 0.85f
        val radius = CornerRadius(cell * 0.18f)
        // Bordered arena.
        drawRoundRect(
            color = AppColors.Primary.copy(alpha = 0.2f),
            cornerRadius = CornerRadius(unit * 0.4f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = unit * 0.08f),
        )
        // L-shaped snake: head bright, body fading.
        val cells = listOf(
            Offset(unit * 0.4f, unit * 0.4f) to 1.0f,
            Offset(unit * 2.0f, unit * 0.4f) to 0.6f,
            Offset(unit * 2.0f, unit * 1.6f) to 0.4f,
            Offset(unit * 2.0f, unit * 2.6f) to 0.2f,
        )
        cells.forEach { (origin, alpha) ->
            drawRoundRect(
                color = AppColors.Primary.copy(alpha = alpha),
                topLeft = origin,
                size = Size(cell, cell),
                cornerRadius = radius,
            )
        }
    }
}

/** The pulsing "▶ PLAY NOW" glass pill at the bottom of the hero. */
@Composable
private fun PlayNowPill(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(AppColors.SurfaceContainer.copy(alpha = 0.7f))
            .border(AppDimensions.GlassBorderWidth, AppColors.Primary.copy(alpha = 0.2f), CircleShape)
            .padding(horizontal = AppDimensions.SpacingLg, vertical = AppDimensions.SpacingSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm),
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = AppColors.Primary,
            modifier = Modifier.size(AppDimensions.IconSize),
        )
        Text(
            text = "PLAY NOW",
            style = AppTheme.type.labelSmall.copy(letterSpacing = 0.5.em),
            color = AppColors.Primary,
        )
    }
}

/** Glass leaderboard panel: header + ranked rows (best first), padded to 5. */
@Composable
private fun RecentScoresCard(scores: List<Int>, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.SurfaceContainerHigh)
                    .padding(horizontal = AppDimensions.SpacingLg, vertical = AppDimensions.SpacingSm),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "RECENT SCORES",
                    style = AppTheme.type.labelSmall.copy(letterSpacing = 0.2.em),
                    color = AppColors.Tertiary,
                )
                Text(
                    text = "NEO-NET",
                    style = AppTheme.type.labelSmall.copy(letterSpacing = 0.2.em),
                    color = AppColors.OnSurfaceVariant,
                )
            }
            for (rank in 0 until ScoreRepository.LEADERBOARD_SIZE) {
                val score = scores.getOrNull(rank)
                if (rank > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppDimensions.Hairline)
                            .background(AppColors.Primary.copy(alpha = 0.1f)),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimensions.SpacingLg, vertical = AppDimensions.SpacingMd),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = (rank + 1).toString().padStart(2, '0'),
                        style = AppTheme.type.labelSmall,
                        color = AppColors.OnSurfaceVariant,
                    )
                    Text(
                        text = score?.toGroupedScore() ?: "—",
                        style = AppTheme.type.scoreDisplay.let {
                            if (score != null) it.withGlow(AppColors.Primary.copy(alpha = 0.4f), blurRadius = 12f) else it
                        },
                        color = if (score != null) AppColors.Primary else AppColors.OnSurfaceVariant.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }
}

/** Full-width glass "⚙ SYSTEM CONFIGURATION" button. */
@Composable
private fun SystemConfigurationButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = AppDimensions.ActionButtonMaxWidth),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .height(AppDimensions.TouchTarget)
                .padding(horizontal = AppDimensions.SpacingLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                tint = AppColors.Tertiary,
                modifier = Modifier.size(AppDimensions.IconSize).padding(end = AppDimensions.SpacingSm),
            )
            Text(
                text = "SYSTEM CONFIGURATION",
                style = AppTheme.type.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = AppColors.Tertiary,
            )
        }
    }
}

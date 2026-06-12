package io.skrastrek.snake.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import io.skrastrek.snake.domain.model.Direction
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.model.SnakeGame
import io.skrastrek.snake.ui.components.BrandWordmark
import io.skrastrek.snake.ui.components.GlassCard
import io.skrastrek.snake.ui.components.GridGlyph
import io.skrastrek.snake.ui.components.NeonBackground
import io.skrastrek.snake.ui.components.NeonIconButton
import io.skrastrek.snake.ui.components.PrimaryButton
import io.skrastrek.snake.ui.format.toPaddedScore
import io.skrastrek.snake.ui.theme.AppColors
import io.skrastrek.snake.ui.theme.AppDimensions
import io.skrastrek.snake.ui.theme.AppTheme
import kotlin.math.abs

/**
 * Stateless Circuit UI for [GameScreen]. Receives only [GameUiState] and a
 * [Modifier]; all interactions are dispatched through [GameUiState.eventSink].
 */
@Composable
fun GameUi(state: GameUiState, modifier: Modifier = Modifier) {
    val eventSink = state.eventSink
    NeonBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GameTopBar(
                status = state.game.status,
                onTogglePause = { eventSink(GameUiEvent.TogglePause) },
                onSettings = { eventSink(GameUiEvent.SettingsClicked) },
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimensions.ContainerPadding, vertical = AppDimensions.SpacingMd),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Scoreboard(score = state.score, highScore = state.highScore)
                Spacer(Modifier.height(AppDimensions.SpacingLg))
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    GameBoard(
                        game = state.game,
                        onSwipe = { eventSink(GameUiEvent.DirectionChanged(it)) },
                    )
                }
                Spacer(Modifier.height(AppDimensions.SpacingMd))
                DirectionPad(onDirection = { eventSink(GameUiEvent.DirectionChanged(it)) })
                Spacer(Modifier.height(AppDimensions.SpacingMd))
                Text(
                    text = "Swipe to change direction",
                    style = AppTheme.type.bodyMedium,
                    color = AppColors.OnSurfaceVariant.copy(alpha = 0.5f),
                )
            }
        }

        if (state.game.status == GameStatus.Paused) {
            PausedOverlay(onResume = { eventSink(GameUiEvent.TogglePause) })
        }
    }
}

/** Top bar: brand mark on the left, pause/resume + settings on the right. */
@Composable
private fun GameTopBar(
    status: GameStatus,
    onTogglePause: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.Background.copy(alpha = 0.7f))
            .safeDrawingPadding()
            .padding(horizontal = AppDimensions.SpacingLg, vertical = AppDimensions.SpacingSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GridGlyph(tint = AppColors.Primary)
            BrandWordmark(modifier = Modifier.padding(start = AppDimensions.SpacingSm))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            val canPause = status == GameStatus.Running || status == GameStatus.Paused
            if (canPause) {
                NeonIconButton(
                    icon = if (status == GameStatus.Paused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = if (status == GameStatus.Paused) "Resume" else "Pause",
                    onClick = onTogglePause,
                    tint = AppColors.Primary,
                )
            }
            NeonIconButton(
                icon = Icons.Filled.Settings,
                contentDescription = "Settings",
                onClick = onSettings,
            )
        }
    }
}

/** Floating glass scoreboard. High-score label is hot-pink to signal importance. */
@Composable
private fun Scoreboard(score: Int, highScore: Int, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AppDimensions.SpacingMd),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ScoreColumn(label = "SCORE", value = score, valueColor = AppColors.Primary)
            ScoreColumn(
                label = "HIGH SCORE",
                value = highScore,
                valueColor = AppColors.Secondary,
                labelColor = AppColors.Secondary,
                alignEnd = true,
            )
        }
    }
}

@Composable
private fun ScoreColumn(
    label: String,
    value: Int,
    valueColor: Color,
    labelColor: Color = AppColors.OnSurfaceVariant,
    alignEnd: Boolean = false,
) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(text = label, style = AppTheme.type.labelSmall, color = labelColor)
        Spacer(Modifier.height(AppDimensions.SpacingXs))
        Text(
            text = value.toPaddedScore(),
            style = AppTheme.type.scoreDisplay,
            color = valueColor,
        )
    }
}

/** The snake arena. Renders the grid, food and snake, and reads swipe gestures. */
@Composable
private fun GameBoard(
    game: SnakeGame,
    onSwipe: (Direction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridColor = AppColors.Tertiary.copy(alpha = 0.10f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(AppDimensions.ShapeOverlay)
            .background(AppColors.SurfaceContainerLowest.copy(alpha = 0.6f))
            .border(
                width = AppDimensions.GlassBorderWidth,
                color = AppColors.Tertiary.copy(alpha = 0.30f),
                shape = AppDimensions.ShapeOverlay,
            )
            .pointerInput(Unit) {
                var totalDrag = Offset.Zero
                detectDragGestures(
                    onDragStart = { totalDrag = Offset.Zero },
                    onDragEnd = {
                        if (abs(totalDrag.x) > abs(totalDrag.y)) {
                            onSwipe(if (totalDrag.x > 0) Direction.Right else Direction.Left)
                        } else {
                            onSwipe(if (totalDrag.y > 0) Direction.Down else Direction.Up)
                        }
                    },
                ) { change, dragAmount ->
                    change.consume()
                    totalDrag += dragAmount
                }
            },
    ) {
        val cell = size.minDimension / game.gridWidth
        val boardW = cell * game.gridWidth
        val boardH = cell * game.gridHeight

        for (i in 0..game.gridWidth) {
            val x = i * cell
            drawLine(gridColor, Offset(x, 0f), Offset(x, boardH), strokeWidth = 1f)
        }
        for (i in 0..game.gridHeight) {
            val y = i * cell
            drawLine(gridColor, Offset(0f, y), Offset(boardW, y), strokeWidth = 1f)
        }

        // Food — circular, hot pink, with a soft glow.
        game.food?.let { food ->
            val center = Offset((food.x + 0.5f) * cell, (food.y + 0.5f) * cell)
            drawCircle(AppColors.Secondary.copy(alpha = 0.25f), radius = cell * 0.7f, center = center)
            drawCircle(AppColors.Secondary, radius = cell * 0.36f, center = center)
        }

        // Snake — rounded electric-lime segments, brighter glowing head.
        val corner = CornerRadius(cell * 0.3f, cell * 0.3f)
        val inset = cell * 0.08f
        game.snake.forEachIndexed { index, point ->
            val isHead = index == 0
            if (isHead) {
                drawRoundRect(
                    color = AppColors.Primary.copy(alpha = 0.25f),
                    topLeft = Offset(point.x * cell - inset, point.y * cell - inset),
                    size = Size(cell + inset * 2, cell + inset * 2),
                    cornerRadius = corner,
                )
            }
            drawRoundRect(
                color = if (isHead) AppColors.Primary else AppColors.Primary.copy(alpha = 0.82f),
                topLeft = Offset(point.x * cell + inset, point.y * cell + inset),
                size = Size(cell - inset * 2, cell - inset * 2),
                cornerRadius = corner,
            )
        }
    }
}

/** Faint on-screen d-pad around a decorative centre dot — swipe is primary. */
@Composable
private fun DirectionPad(
    onDirection: (Direction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PadArrow(Icons.Filled.KeyboardArrowUp, "Up") { onDirection(Direction.Up) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            PadArrow(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Left") { onDirection(Direction.Left) }
            Box(
                modifier = Modifier.size(AppDimensions.TouchTarget),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.SpacingSm)
                        .clip(CircleShape)
                        .background(AppColors.Tertiary.copy(alpha = 0.4f)),
                )
            }
            PadArrow(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Right") { onDirection(Direction.Right) }
        }
        PadArrow(Icons.Filled.KeyboardArrowDown, "Down") { onDirection(Direction.Down) }
    }
}

@Composable
private fun PadArrow(icon: ImageVector, description: String, onClick: () -> Unit) {
    NeonIconButton(
        icon = icon,
        contentDescription = description,
        onClick = onClick,
        tint = AppColors.OnSurfaceVariant.copy(alpha = 0.4f),
    )
}

/** Centered glass overlay shown while paused. */
@Composable
private fun PausedOverlay(onResume: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background.copy(alpha = 0.75f))
            .padding(AppDimensions.ContainerPadding),
        contentAlignment = Alignment.Center,
    ) {
        GlassCard {
            Column(
                modifier = Modifier.padding(AppDimensions.SpacingXl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingLg),
            ) {
                Text(
                    text = "PAUSED",
                    style = AppTheme.type.headlineLargeMobile,
                    color = AppColors.Tertiary,
                    textAlign = TextAlign.Center,
                )
                PrimaryButton(label = "RESUME", onClick = onResume, leadingIcon = Icons.Filled.PlayArrow)
            }
        }
    }
}

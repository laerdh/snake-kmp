package io.skrastrek.snake.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.Direction
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.model.SnakeGame
import io.skrastrek.snake.ui.theme.AppColors
import io.skrastrek.snake.ui.theme.AppDimensions
import io.skrastrek.snake.ui.theme.AppTypography
import kotlin.math.abs

/**
 * Stateless Circuit UI for [GameScreen]. Receives only [GameUiState] and a
 * [Modifier]; all interactions are dispatched through [GameUiState.eventSink].
 */
@Composable
fun GameUi(state: GameUiState, modifier: Modifier = Modifier) {
    val eventSink = state.eventSink
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = AppDimensions.ContainerPadding, vertical = AppDimensions.SpacingMd),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Scoreboard(score = state.game.score, highScore = state.highScore)
            Spacer(Modifier.height(AppDimensions.SpacingLg))
            DifficultyChips(
                selected = state.difficulty,
                onSelect = { eventSink(GameUiEvent.DifficultyChanged(it)) },
            )
            Spacer(Modifier.height(AppDimensions.SpacingMd))
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
            DirectionPad(
                status = state.game.status,
                onDirection = { eventSink(GameUiEvent.DirectionChanged(it)) },
                onTogglePause = { eventSink(GameUiEvent.TogglePause) },
            )
        }

        when (state.game.status) {
            GameStatus.GameOver -> GameOverOverlay(
                score = state.game.score,
                isNewHighScore = state.isNewHighScore,
                onRestart = { eventSink(GameUiEvent.Restart) },
            )

            GameStatus.Paused -> PausedOverlay(
                onResume = { eventSink(GameUiEvent.TogglePause) },
            )

            GameStatus.Ready -> ReadyHint()
            GameStatus.Running -> Unit
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
                label = "HIGH",
                value = highScore,
                valueColor = AppColors.OnSurface,
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
        Text(text = label, style = AppTypography.LabelSmall, color = labelColor)
        Spacer(Modifier.height(AppDimensions.SpacingXs))
        Text(
            text = value.toString().padStart(3, '0'),
            style = AppTypography.ScoreDisplay,
            color = valueColor,
        )
    }
}

/** Pill-shaped difficulty selector chips. */
@Composable
private fun DifficultyChips(
    selected: Difficulty,
    onSelect: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm),
    ) {
        Difficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == selected
            val bg = if (isSelected) AppColors.SecondaryContainer else AppColors.SurfaceContainer
            val fg = if (isSelected) Color.White else AppColors.OnSurfaceVariant
            Text(
                text = difficulty.label,
                style = AppTypography.LabelSmall,
                color = fg,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(bg)
                    .clickable { onSelect(difficulty) }
                    .padding(horizontal = AppDimensions.SpacingMd, vertical = AppDimensions.SpacingSm),
            )
        }
    }
}

/** The snake arena. Renders the grid, food and snake, and reads swipe gestures. */
@Composable
private fun GameBoard(
    game: SnakeGame,
    onSwipe: (Direction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridColor = AppColors.OutlineVariant.copy(alpha = 0.35f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(AppDimensions.ShapeOverlay)
            .background(AppColors.SurfaceContainerLowest)
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

        // Subtle grid
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

/** On-screen D-pad with a central pause/resume control. */
@Composable
private fun DirectionPad(
    status: GameStatus,
    onDirection: (Direction) -> Unit,
    onTogglePause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm),
    ) {
        PadButton(symbol = "▲", onClick = { onDirection(Direction.Up) })
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm)) {
            PadButton(symbol = "◀", onClick = { onDirection(Direction.Left) })
            PadButton(
                symbol = if (status == GameStatus.Paused) "▶︎" else "❚❚",
                onClick = onTogglePause,
                accent = true,
            )
            PadButton(symbol = "▶", onClick = { onDirection(Direction.Right) })
        }
        PadButton(symbol = "▼", onClick = { onDirection(Direction.Down) })
    }
}

@Composable
private fun PadButton(
    symbol: String,
    onClick: () -> Unit,
    accent: Boolean = false,
) {
    val border = if (accent) AppColors.Primary else AppColors.Tertiary
    Box(
        modifier = Modifier
            .size(AppDimensions.TouchTarget)
            .clip(CircleShape)
            .background(AppColors.SurfaceContainer.copy(alpha = 0.6f))
            .border(AppDimensions.SecondaryButtonBorderWidth, border.copy(alpha = 0.7f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = symbol, color = border, style = AppTypography.BodyMedium)
    }
}

/** Glassmorphism container: translucent fill + thin cyan stroke. */
@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(AppDimensions.ShapeOverlay)
            .background(AppColors.SurfaceContainer.copy(alpha = 0.70f))
            .border(
                width = AppDimensions.GlassBorderWidth,
                color = AppColors.Tertiary.copy(alpha = 0.30f),
                shape = AppDimensions.ShapeOverlay,
            ),
    ) {
        content()
    }
}

/** Centered overlay shown when the round ends. */
@Composable
private fun GameOverOverlay(
    score: Int,
    isNewHighScore: Boolean,
    onRestart: () -> Unit,
) {
    OverlayScrim {
        GlassCard {
            Column(
                modifier = Modifier.padding(AppDimensions.SpacingXl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingMd),
            ) {
                Text(
                    text = "GAME OVER",
                    style = AppTypography.HeadlineLargeMobile,
                    color = AppColors.Error,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = if (isNewHighScore) "NEW HIGH SCORE: $score" else "SCORE: $score",
                    style = AppTypography.ScoreDisplay,
                    color = if (isNewHighScore) AppColors.Secondary else AppColors.OnSurface,
                )
                PrimaryButton(label = "PLAY AGAIN", onClick = onRestart)
            }
        }
    }
}

/** Centered overlay shown while paused. */
@Composable
private fun PausedOverlay(onResume: () -> Unit) {
    OverlayScrim {
        GlassCard {
            Column(
                modifier = Modifier.padding(AppDimensions.SpacingXl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingMd),
            ) {
                Text(text = "PAUSED", style = AppTypography.HeadlineLargeMobile, color = AppColors.Tertiary)
                PrimaryButton(label = "RESUME", onClick = onResume)
            }
        }
    }
}

/** Hint shown on a fresh board before the first move. */
@Composable
private fun ReadyHint() {
    Text(
        text = "SWIPE OR TAP TO START",
        style = AppTypography.LabelSmall,
        color = AppColors.OnSurfaceVariant,
        modifier = Modifier
            .clip(CircleShape)
            .background(AppColors.SurfaceContainer.copy(alpha = 0.7f))
            .padding(horizontal = AppDimensions.SpacingMd, vertical = AppDimensions.SpacingSm),
    )
}

@Composable
private fun OverlayScrim(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background.copy(alpha = 0.75f))
            .padding(AppDimensions.ContainerPadding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** Solid electric-lime primary button with black text. */
@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = AppTypography.BodyMedium.copy(fontWeight = FontWeight.Bold),
        color = AppColors.OnPrimary,
        modifier = Modifier
            .clip(AppDimensions.ShapeCard)
            .background(AppColors.Primary)
            .clickable(onClick = onClick)
            .width(180.dp)
            .padding(horizontal = AppDimensions.SpacingXl, vertical = AppDimensions.SpacingMd),
        textAlign = TextAlign.Center,
    )
}

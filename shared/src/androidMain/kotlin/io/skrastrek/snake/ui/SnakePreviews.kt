package io.skrastrek.snake.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.model.PlayerProfile
import io.skrastrek.snake.ui.game.GameUi
import io.skrastrek.snake.ui.game.GameUiState
import io.skrastrek.snake.ui.gameover.GameOverUi
import io.skrastrek.snake.ui.gameover.GameOverUiState
import io.skrastrek.snake.ui.settings.SettingsUi
import io.skrastrek.snake.ui.settings.SettingsUiState
import io.skrastrek.snake.ui.start.StartUi
import io.skrastrek.snake.ui.start.StartUiState
import io.skrastrek.snake.ui.theme.SnakeTheme

/** Design-time previews of each screen, fed hardcoded states. */

@Preview
@Composable
private fun StartUiPreview() {
    SnakeTheme {
        StartUi(
            state = StartUiState(
                recentScores = listOf(14_250, 8_420, 5_110, 4_900, 3_200),
                eventSink = {},
            ),
        )
    }
}

@Preview
@Composable
private fun GameUiPreview() {
    val game = GameEngine().newGame().copy(status = GameStatus.Running, applesEaten = 10)
    SnakeTheme {
        GameUi(
            state = GameUiState(
                game = game,
                score = 250,
                highScore = 1_042,
                eventSink = {},
            ),
        )
    }
}

@Preview
@Composable
private fun GameOverUiPreview() {
    SnakeTheme {
        GameOverUi(
            state = GameOverUiState(
                points = 14_250,
                applesEaten = 42,
                elapsedMillis = 194_000L,
                isNewBest = true,
                eventSink = {},
            ),
        )
    }
}

@Preview
@Composable
private fun SettingsUiPreview() {
    SnakeTheme {
        SettingsUi(
            state = SettingsUiState(
                profile = PlayerProfile.Default,
                speed = Difficulty.Chill,
                eventSink = {},
            ),
        )
    }
}

package io.skrastrek.snake.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.ui.theme.SnakeTheme

/** Design-time preview of the gameplay screen with a mid-round board. */
@Preview
@Composable
private fun GameUiPreview() {
    val game = GameEngine().newGame().copy(status = GameStatus.Running, score = 7)
    SnakeTheme {
        GameUi(
            state = GameUiState(
                game = game,
                highScore = 42,
                difficulty = Difficulty.Normal,
                isNewHighScore = false,
                eventSink = {},
            ),
        )
    }
}

package io.skrastrek.snake.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.Direction
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.model.SnakeGame
import io.skrastrek.snake.domain.usecase.ObserveHighScoreUseCase
import io.skrastrek.snake.domain.usecase.SubmitScoreUseCase
import io.skrastrek.snake.platform.CommonParcelize
import kotlinx.coroutines.delay

/**
 * The single Snake gameplay destination.
 *
 * Modelled as a Circuit [Screen]; for a one-screen game there are no navigation
 * arguments. Kept `Parcelable` on Android (via [CommonParcelize]) so the Circuit
 * backstack can be saved and restored.
 */
@CommonParcelize
object GameScreen : Screen

/**
 * Immutable render state for [GameScreen]. The board is always in a renderable
 * shape (no loading/error phases for an offline game), so a single data class is
 * used rather than a sealed hierarchy.
 *
 * @property game current game snapshot.
 * @property highScore the best score recorded so far.
 * @property difficulty selected speed tier.
 * @property isNewHighScore `true` once the just-finished round set a new record.
 * @property eventSink channel for user actions (always the last property).
 */
data class GameUiState(
    val game: SnakeGame,
    val highScore: Int,
    val difficulty: Difficulty,
    val isNewHighScore: Boolean,
    val eventSink: (GameUiEvent) -> Unit,
) : CircuitUiState

/** User actions sent from the UI back to [GamePresenter]. */
sealed interface GameUiEvent : CircuitUiEvent {
    /** Steer the snake. */
    data class DirectionChanged(val direction: Direction) : GameUiEvent

    /** Pause or resume the current round. */
    data object TogglePause : GameUiEvent

    /** Abandon the current round and deal a fresh board. */
    data object Restart : GameUiEvent

    /** Switch speed tier (also restarts the round). */
    data class DifficultyChanged(val difficulty: Difficulty) : GameUiEvent
}

/**
 * Holds and advances all game state for [GameScreen]. The game loop lives here —
 * never in the UI — and is driven by a cancellable [LaunchedEffect] keyed on the
 * run state, so pausing/finishing cleanly stops the ticker.
 */
class GamePresenter(
    private val engine: GameEngine,
    private val observeHighScore: ObserveHighScoreUseCase,
    private val submitScore: SubmitScoreUseCase,
) : Presenter<GameUiState> {

    @Composable
    override fun present(): GameUiState {
        var difficulty by rememberRetained { mutableStateOf(Difficulty.Normal) }
        var game by rememberRetained { mutableStateOf(engine.newGame()) }
        var isNewHighScore by rememberRetained { mutableStateOf(false) }
        val highScore by observeHighScore().collectAsState(initial = 0)

        // The game loop: re-launched whenever run state or speed changes. While
        // Running it advances the snake every `tickMillis`; any other status
        // leaves the effect idle (cancelled).
        LaunchedEffect(game.status, difficulty) {
            if (game.status == GameStatus.Running) {
                while (true) {
                    delay(difficulty.tickMillis)
                    game = engine.tick(game)
                }
            }
        }

        // One-shot side effect: persist the score when the round ends.
        LaunchedEffect(game.status) {
            if (game.status == GameStatus.GameOver) {
                isNewHighScore = submitScore(game.score)
            }
        }

        return GameUiState(
            game = game,
            highScore = highScore,
            difficulty = difficulty,
            isNewHighScore = isNewHighScore,
            eventSink = { event ->
                when (event) {
                    is GameUiEvent.DirectionChanged ->
                        game = engine.changeDirection(game, event.direction)

                    GameUiEvent.TogglePause ->
                        game = engine.togglePause(game)

                    GameUiEvent.Restart -> {
                        isNewHighScore = false
                        game = engine.newGame()
                    }

                    is GameUiEvent.DifficultyChanged -> {
                        isNewHighScore = false
                        difficulty = event.difficulty
                        game = engine.newGame()
                    }
                }
            },
        )
    }
}

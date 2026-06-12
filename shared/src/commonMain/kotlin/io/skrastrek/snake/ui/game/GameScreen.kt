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
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.model.Direction
import io.skrastrek.snake.domain.model.GameSettings
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.model.SnakeGame
import io.skrastrek.snake.domain.scoreWith
import io.skrastrek.snake.domain.usecase.ObserveHighScoreUseCase
import io.skrastrek.snake.domain.usecase.ObserveSettingsUseCase
import io.skrastrek.snake.domain.usecase.SubmitScoreUseCase
import io.skrastrek.snake.platform.CommonParcelize
import io.skrastrek.snake.ui.gameover.GameOverScreen
import io.skrastrek.snake.ui.settings.SettingsScreen
import kotlinx.coroutines.delay

/**
 * The Snake gameplay destination. One screen, no navigation arguments. Kept
 * `Parcelable` (via [CommonParcelize]) so the Circuit backstack can be restored.
 */
@CommonParcelize
object GameScreen : Screen

/**
 * Immutable render state for [GameScreen].
 *
 * @property game current game snapshot.
 * @property score the live points total (apples × speed tier).
 * @property highScore the best points total recorded so far.
 * @property eventSink channel for user actions (always last).
 */
data class GameUiState(
    val game: SnakeGame,
    val score: Int,
    val highScore: Int,
    val eventSink: (GameUiEvent) -> Unit,
) : CircuitUiState

/** User actions sent from the UI back to [GamePresenter]. */
sealed interface GameUiEvent : CircuitUiEvent {
    /** Steer the snake. */
    data class DirectionChanged(val direction: Direction) : GameUiEvent

    /** Pause or resume the current round. */
    data object TogglePause : GameUiEvent

    /** Open the Settings screen. */
    data object SettingsClicked : GameUiEvent
}

/**
 * Holds and advances all game state for [GameScreen]. The game loop lives here —
 * never in the UI — driven by a cancellable [LaunchedEffect] keyed on run state +
 * speed. The active speed tier is read from settings, so changes made on the
 * Settings screen apply on return. When the round ends, the score is persisted and
 * the player is routed to the [GameOverScreen].
 */
class GamePresenter(
    private val navigator: Navigator,
    private val engine: GameEngine,
    private val observeHighScore: ObserveHighScoreUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    private val submitScore: SubmitScoreUseCase,
) : Presenter<GameUiState> {

    @Composable
    override fun present(): GameUiState {
        val settings by observeSettings().collectAsState(initial = GameSettings.Default)
        val difficulty = settings.difficulty
        var game by rememberRetained { mutableStateOf(engine.newGame()) }
        val highScore by observeHighScore().collectAsState(initial = 0)

        // The game loop: re-launched whenever run state or speed changes. While
        // Running it advances the snake every `tickMillis`; any other status
        // leaves the effect idle (cancelled).
        LaunchedEffect(game.status, difficulty) {
            if (game.status == GameStatus.Running) {
                while (true) {
                    delay(difficulty.tickMillis)
                    game = engine.tick(game, stepMillis = difficulty.tickMillis)
                }
            }
        }

        // One-shot side effect: when the round ends, persist the score and route
        // to the Game Over screen with the final figures.
        LaunchedEffect(game.status) {
            if (game.status == GameStatus.GameOver) {
                val result = game.scoreWith(difficulty)
                val isNewBest = submitScore(result.points)
                navigator.goTo(
                    GameOverScreen(
                        points = result.points,
                        applesEaten = result.applesEaten,
                        elapsedMillis = result.elapsedMillis,
                        isNewBest = isNewBest,
                    ),
                )
            }
        }

        return GameUiState(
            game = game,
            score = game.applesEaten * difficulty.pointsPerApple,
            highScore = highScore,
            eventSink = { event ->
                when (event) {
                    is GameUiEvent.DirectionChanged ->
                        game = engine.changeDirection(game, event.direction)

                    GameUiEvent.TogglePause ->
                        game = engine.togglePause(game)

                    GameUiEvent.SettingsClicked ->
                        navigator.goTo(SettingsScreen)
                }
            },
        )
    }
}

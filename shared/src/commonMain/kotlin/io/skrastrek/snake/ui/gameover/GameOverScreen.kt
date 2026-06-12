package io.skrastrek.snake.ui.gameover

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import io.skrastrek.snake.platform.CommonParcelize
import io.skrastrek.snake.ui.game.GameScreen
import io.skrastrek.snake.ui.settings.SettingsScreen
import io.skrastrek.snake.ui.start.StartScreen

/**
 * The end-of-round summary destination. The final figures are carried as screen
 * arguments (so the screen is self-contained and the underlying game presenter can
 * be discarded), and are `Parcelable` via [CommonParcelize].
 *
 * @property points headline TOTAL SCORE.
 * @property applesEaten apples eaten this round.
 * @property elapsedMillis survival time, in milliseconds.
 * @property isNewBest whether this round set a new high score.
 */
@CommonParcelize
data class GameOverScreen(
    val points: Int,
    val applesEaten: Int,
    val elapsedMillis: Long,
    val isNewBest: Boolean,
) : Screen

/**
 * Immutable render state for [GameOverScreen] — mirrors the screen arguments plus
 * the [eventSink].
 */
data class GameOverUiState(
    val points: Int,
    val applesEaten: Int,
    val elapsedMillis: Long,
    val isNewBest: Boolean,
    val eventSink: (GameOverUiEvent) -> Unit,
) : CircuitUiState

/** User actions sent from the UI back to [GameOverPresenter]. */
sealed interface GameOverUiEvent : CircuitUiEvent {
    /** Start a fresh round. */
    data object PlayAgainClicked : GameOverUiEvent

    /** Return to the title screen. */
    data object HomeClicked : GameOverUiEvent

    /** Open the Settings screen. */
    data object SettingsClicked : GameOverUiEvent
}

/**
 * Presenter for [GameOverScreen]. Holds no game logic — it simply surfaces the
 * round result and resets the backstack on navigation (a fresh [GameScreen] for
 * "play again", the [StartScreen] root for "home").
 */
class GameOverPresenter(
    private val screen: GameOverScreen,
    private val navigator: Navigator,
) : Presenter<GameOverUiState> {

    @Composable
    override fun present(): GameOverUiState =
        GameOverUiState(
            points = screen.points,
            applesEaten = screen.applesEaten,
            elapsedMillis = screen.elapsedMillis,
            isNewBest = screen.isNewBest,
            eventSink = { event ->
                when (event) {
                    GameOverUiEvent.PlayAgainClicked -> {
                        navigator.resetRoot(StartScreen)
                        navigator.goTo(GameScreen)
                    }

                    GameOverUiEvent.HomeClicked -> navigator.resetRoot(StartScreen)
                    GameOverUiEvent.SettingsClicked -> navigator.goTo(SettingsScreen)
                }
            },
        )
}

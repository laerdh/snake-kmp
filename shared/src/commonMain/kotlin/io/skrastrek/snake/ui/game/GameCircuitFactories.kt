package io.skrastrek.snake.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.usecase.ObserveHighScoreUseCase
import io.skrastrek.snake.domain.usecase.SubmitScoreUseCase

/**
 * Circuit [Presenter.Factory] that wires [GameScreen] to [GamePresenter].
 * Dependencies are injected by Koin; the factory itself never reaches into DI.
 */
class GamePresenterFactory(
    private val engine: GameEngine,
    private val observeHighScore: ObserveHighScoreUseCase,
    private val submitScore: SubmitScoreUseCase,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? = when (screen) {
        is GameScreen -> GamePresenter(engine, observeHighScore, submitScore)
        else -> null
    }
}

/** Circuit [Ui.Factory] that renders [GameScreen] with [GameUi]. */
class GameUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is GameScreen -> object : Ui<GameUiState> {
            @Composable
            override fun Content(state: GameUiState, modifier: Modifier) {
                GameUi(state, modifier)
            }
        }

        else -> null
    }
}

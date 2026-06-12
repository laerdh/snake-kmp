package io.skrastrek.snake.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.usecase.ObserveHighScoreUseCase
import io.skrastrek.snake.domain.usecase.ObserveProfileUseCase
import io.skrastrek.snake.domain.usecase.ObserveRecentScoresUseCase
import io.skrastrek.snake.domain.usecase.ObserveSettingsUseCase
import io.skrastrek.snake.domain.usecase.ResetProgressUseCase
import io.skrastrek.snake.domain.usecase.SubmitScoreUseCase
import io.skrastrek.snake.domain.usecase.UpdateSettingsUseCase
import io.skrastrek.snake.ui.game.GamePresenter
import io.skrastrek.snake.ui.game.GameScreen
import io.skrastrek.snake.ui.game.GameUi
import io.skrastrek.snake.ui.game.GameUiState
import io.skrastrek.snake.ui.gameover.GameOverPresenter
import io.skrastrek.snake.ui.gameover.GameOverScreen
import io.skrastrek.snake.ui.gameover.GameOverUi
import io.skrastrek.snake.ui.gameover.GameOverUiState
import io.skrastrek.snake.ui.settings.SettingsPresenter
import io.skrastrek.snake.ui.settings.SettingsScreen
import io.skrastrek.snake.ui.settings.SettingsUi
import io.skrastrek.snake.ui.settings.SettingsUiState
import io.skrastrek.snake.ui.start.StartPresenter
import io.skrastrek.snake.ui.start.StartScreen
import io.skrastrek.snake.ui.start.StartUi
import io.skrastrek.snake.ui.start.StartUiState

/**
 * Single Circuit [Presenter.Factory] for every screen in the app. Circuit hands
 * the [Navigator] to [create] (it is never Koin-injected); the factory forwards it
 * to the presenters that navigate. Use-case/engine dependencies are injected by Koin.
 */
class SnakePresenterFactory(
    private val engine: GameEngine,
    private val observeHighScore: ObserveHighScoreUseCase,
    private val observeRecentScores: ObserveRecentScoresUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    private val observeProfile: ObserveProfileUseCase,
    private val submitScore: SubmitScoreUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val resetProgress: ResetProgressUseCase,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? = when (screen) {
        is StartScreen -> StartPresenter(navigator, observeRecentScores)
        is GameScreen -> GamePresenter(navigator, engine, observeHighScore, observeSettings, submitScore)
        is GameOverScreen -> GameOverPresenter(screen, navigator)
        is SettingsScreen -> SettingsPresenter(navigator, observeSettings, observeProfile, updateSettings, resetProgress)
        else -> null
    }
}

/** Single Circuit [Ui.Factory] mapping each screen to its stateless `Ui`. */
class SnakeUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is StartScreen -> ui<StartUiState> { state, modifier -> StartUi(state, modifier) }
        is GameScreen -> ui<GameUiState> { state, modifier -> GameUi(state, modifier) }
        is GameOverScreen -> ui<GameOverUiState> { state, modifier -> GameOverUi(state, modifier) }
        is SettingsScreen -> ui<SettingsUiState> { state, modifier -> SettingsUi(state, modifier) }
        else -> null
    }
}

/** Small helper to build a stateless [Ui] from a composable lambda. */
private inline fun <UiState : com.slack.circuit.runtime.CircuitUiState> ui(
    crossinline content: @Composable (UiState, Modifier) -> Unit,
): Ui<UiState> = object : Ui<UiState> {
    @Composable
    override fun Content(state: UiState, modifier: Modifier) = content(state, modifier)
}

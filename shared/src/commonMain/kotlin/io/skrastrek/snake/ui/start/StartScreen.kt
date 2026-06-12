package io.skrastrek.snake.ui.start

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import io.skrastrek.snake.domain.usecase.ObserveRecentScoresUseCase
import io.skrastrek.snake.platform.CommonParcelize
import io.skrastrek.snake.ui.game.GameScreen
import io.skrastrek.snake.ui.settings.SettingsScreen

/**
 * The title / launch destination ("SNAKE.NEO"). The root of the backstack: shows
 * the leaderboard and routes the player into a game or the settings.
 */
@CommonParcelize
object StartScreen : Screen

/**
 * Immutable render state for [StartScreen].
 *
 * @property recentScores leaderboard entries (best first), already capped.
 * @property eventSink channel for user actions (always last).
 */
data class StartUiState(
    val recentScores: List<Int>,
    val eventSink: (StartUiEvent) -> Unit,
) : CircuitUiState

/** User actions sent from the UI back to [StartPresenter]. */
sealed interface StartUiEvent : CircuitUiEvent {
    /** Start a new game. */
    data object PlayClicked : StartUiEvent

    /** Open system configuration (Settings). */
    data object SettingsClicked : StartUiEvent
}

/**
 * Presenter for [StartScreen]. Streams the leaderboard and forwards navigation
 * intents to the Circuit [navigator] (supplied by the factory — never injected).
 */
class StartPresenter(
    private val navigator: Navigator,
    private val observeRecentScores: ObserveRecentScoresUseCase,
) : Presenter<StartUiState> {

    @Composable
    override fun present(): StartUiState {
        val recentScores by observeRecentScores().collectAsState(initial = emptyList())

        return StartUiState(
            recentScores = recentScores,
            eventSink = { event ->
                when (event) {
                    StartUiEvent.PlayClicked -> navigator.goTo(GameScreen)
                    StartUiEvent.SettingsClicked -> navigator.goTo(SettingsScreen)
                }
            },
        )
    }
}

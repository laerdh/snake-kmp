package io.skrastrek.snake.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.GameSettings
import io.skrastrek.snake.domain.model.PlayerProfile
import io.skrastrek.snake.domain.usecase.ObserveProfileUseCase
import io.skrastrek.snake.domain.usecase.ObserveSettingsUseCase
import io.skrastrek.snake.domain.usecase.ResetProgressUseCase
import io.skrastrek.snake.domain.usecase.UpdateSettingsUseCase
import io.skrastrek.snake.platform.CommonParcelize
import kotlinx.coroutines.launch

/** The system-configuration destination ("SNAKE.NEO" settings). */
@CommonParcelize
object SettingsScreen : Screen

/**
 * Immutable render state for [SettingsScreen].
 *
 * @property profile the player's identity card.
 * @property speed the currently-selected (possibly unsaved) speed tier.
 * @property eventSink channel for user actions (always last).
 */
data class SettingsUiState(
    val profile: PlayerProfile,
    val speed: Difficulty,
    val eventSink: (SettingsUiEvent) -> Unit,
) : CircuitUiState {
    /** Position of [speed] on the 0..2 GAME SPEED slider. */
    val speedIndex: Int get() = speed.sliderIndex
}

/** User actions sent from the UI back to [SettingsPresenter]. */
sealed interface SettingsUiEvent : CircuitUiEvent {
    /** Drag the GAME SPEED slider to a new 0..2 position. */
    data class SpeedChanged(val index: Int) : SettingsUiEvent

    /** Persist the edited settings and close. */
    data object SaveClicked : SettingsUiEvent

    /** Clear all recorded scores. */
    data object ResetClicked : SettingsUiEvent

    /** Dismiss without an explicit save (back arrow / close). */
    data object CloseClicked : SettingsUiEvent
}

/**
 * Presenter for [SettingsScreen]. Edits are held locally (so the slider is
 * responsive) and only committed to the [UpdateSettingsUseCase] on "SAVE SETTINGS".
 * Suspending actions run on a UI-scoped [rememberCoroutineScope].
 */
class SettingsPresenter(
    private val navigator: Navigator,
    private val observeSettings: ObserveSettingsUseCase,
    private val observeProfile: ObserveProfileUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val resetProgress: ResetProgressUseCase,
) : Presenter<SettingsUiState> {

    @Composable
    override fun present(): SettingsUiState {
        val scope = rememberCoroutineScope()
        val stored by observeSettings().collectAsState(initial = GameSettings.Default)
        val profile by observeProfile().collectAsState(initial = PlayerProfile.Default)

        // Local, unsaved slider position; `null` until the player drags it, at
        // which point it overrides the stored value.
        var editedIndex by rememberRetained { mutableStateOf<Int?>(null) }
        val speed = Difficulty.fromSliderIndex(editedIndex ?: stored.difficulty.sliderIndex)

        return SettingsUiState(
            profile = profile,
            speed = speed,
            eventSink = { event ->
                when (event) {
                    is SettingsUiEvent.SpeedChanged -> editedIndex = event.index

                    SettingsUiEvent.SaveClicked -> scope.launch {
                        updateSettings(GameSettings(difficulty = speed))
                        navigator.pop()
                    }

                    SettingsUiEvent.ResetClicked -> scope.launch { resetProgress() }

                    SettingsUiEvent.CloseClicked -> navigator.pop()
                }
            },
        )
    }
}

package io.skrastrek.snake

import androidx.compose.runtime.Composable
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import io.skrastrek.snake.ui.game.GameScreen
import io.skrastrek.snake.ui.theme.SnakeTheme
import org.koin.compose.koinInject

/**
 * Shared application root. Wraps the Circuit backstack in the app theme and is
 * the single entry point used by both the Android `MainActivity` and the iOS
 * `MainViewController`.
 *
 * The [Circuit] instance is provided by Koin, so [initKoin][io.skrastrek.snake.di.initKoin]
 * must run before this composable is shown.
 */
@Composable
fun App() {
    SnakeTheme {
        val circuit: Circuit = koinInject()
        CircuitCompositionLocals(circuit) {
            val backStack = rememberSaveableBackStack(root = GameScreen)
            val navigator = rememberCircuitNavigator(backStack) { /* root pop: nothing to do */ }
            NavigableCircuitContent(navigator = navigator, backStack = backStack)
        }
    }
}

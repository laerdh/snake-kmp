package io.skrastrek.snake.di

import com.slack.circuit.foundation.Circuit
import io.skrastrek.snake.ui.SnakePresenterFactory
import io.skrastrek.snake.ui.SnakeUiFactory
import org.koin.dsl.module

/**
 * Assembles the Circuit instance from the registered presenter and UI factories.
 * New screens are added inside [SnakePresenterFactory] / [SnakeUiFactory].
 */
val circuitModule = module {
    factory {
        SnakePresenterFactory(
            engine = get(),
            observeHighScore = get(),
            observeRecentScores = get(),
            observeSettings = get(),
            observeProfile = get(),
            submitScore = get(),
            updateSettings = get(),
            resetProgress = get(),
        )
    }
    single {
        Circuit.Builder()
            .addPresenterFactory(get<SnakePresenterFactory>())
            .addUiFactory(SnakeUiFactory())
            .build()
    }
}

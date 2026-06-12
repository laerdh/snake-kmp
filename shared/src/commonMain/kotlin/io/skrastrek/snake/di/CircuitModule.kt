package io.skrastrek.snake.di

import com.slack.circuit.foundation.Circuit
import io.skrastrek.snake.ui.game.GamePresenterFactory
import io.skrastrek.snake.ui.game.GameUiFactory
import org.koin.dsl.module

/**
 * Assembles the Circuit instance from the registered presenter and UI factories.
 * New screens are added by registering their factories here.
 */
val circuitModule = module {
    factory { GamePresenterFactory(engine = get(), observeHighScore = get(), submitScore = get()) }
    single {
        Circuit.Builder()
            .addPresenterFactory(get<GamePresenterFactory>())
            .addUiFactory(GameUiFactory())
            .build()
    }
}

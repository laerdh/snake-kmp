package io.skrastrek.snake.di

import io.skrastrek.snake.data.InMemoryHighScoreRepository
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.HighScoreRepository
import org.koin.dsl.module

/** Data layer: repositories, data sources and the (stateless) game engine. */
val dataModule = module {
    single<HighScoreRepository> { InMemoryHighScoreRepository() }
    single { GameEngine() }
}

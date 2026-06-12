package io.skrastrek.snake.di

import io.skrastrek.snake.data.InMemoryProfileRepository
import io.skrastrek.snake.data.InMemoryScoreRepository
import io.skrastrek.snake.data.InMemorySettingsRepository
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.ProfileRepository
import io.skrastrek.snake.domain.ScoreRepository
import io.skrastrek.snake.domain.SettingsRepository
import org.koin.dsl.module

/** Data layer: repositories, data sources and the (stateless) game engine. */
val dataModule = module {
    single<ScoreRepository> { InMemoryScoreRepository() }
    single<SettingsRepository> { InMemorySettingsRepository() }
    single<ProfileRepository> { InMemoryProfileRepository() }
    single { GameEngine() }
}

package io.skrastrek.snake.di

import io.skrastrek.snake.domain.usecase.ObserveHighScoreUseCase
import io.skrastrek.snake.domain.usecase.SubmitScoreUseCase
import org.koin.dsl.module

/** Domain layer: use cases (one public function each). */
val domainModule = module {
    factory { ObserveHighScoreUseCase(get()) }
    factory { SubmitScoreUseCase(get()) }
}

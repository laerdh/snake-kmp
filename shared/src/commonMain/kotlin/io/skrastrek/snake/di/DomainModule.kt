package io.skrastrek.snake.di

import io.skrastrek.snake.domain.usecase.ObserveHighScoreUseCase
import io.skrastrek.snake.domain.usecase.ObserveProfileUseCase
import io.skrastrek.snake.domain.usecase.ObserveRecentScoresUseCase
import io.skrastrek.snake.domain.usecase.ObserveSettingsUseCase
import io.skrastrek.snake.domain.usecase.ResetProgressUseCase
import io.skrastrek.snake.domain.usecase.SubmitScoreUseCase
import io.skrastrek.snake.domain.usecase.UpdateSettingsUseCase
import org.koin.dsl.module

/** Domain layer: use cases (one public function each). */
val domainModule = module {
    factory { ObserveHighScoreUseCase(get()) }
    factory { ObserveRecentScoresUseCase(get()) }
    factory { SubmitScoreUseCase(get()) }
    factory { ResetProgressUseCase(get()) }
    factory { ObserveSettingsUseCase(get()) }
    factory { UpdateSettingsUseCase(get()) }
    factory { ObserveProfileUseCase(get()) }
}

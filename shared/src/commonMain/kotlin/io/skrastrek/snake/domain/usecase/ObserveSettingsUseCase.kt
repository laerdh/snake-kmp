package io.skrastrek.snake.domain.usecase

import io.skrastrek.snake.domain.SettingsRepository
import io.skrastrek.snake.domain.model.GameSettings
import kotlinx.coroutines.flow.Flow

/** Streams the player's current [GameSettings]. */
class ObserveSettingsUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<GameSettings> = repository.settings()
}

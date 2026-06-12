package io.skrastrek.snake.domain.usecase

import io.skrastrek.snake.domain.SettingsRepository
import io.skrastrek.snake.domain.model.GameSettings

/** Persists edited [GameSettings] (the Settings "SAVE SETTINGS" action). */
class UpdateSettingsUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(settings: GameSettings) = repository.update(settings)
}

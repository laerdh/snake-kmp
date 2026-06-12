package io.skrastrek.snake.data

import io.skrastrek.snake.domain.SettingsRepository
import io.skrastrek.snake.domain.model.GameSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory [SettingsRepository], seeded with [GameSettings.Default]. Settings
 * survive for the lifetime of the process only.
 */
class InMemorySettingsRepository : SettingsRepository {
    private val state = MutableStateFlow(GameSettings.Default)

    override fun settings(): Flow<GameSettings> = state.asStateFlow()

    override suspend fun update(settings: GameSettings) {
        state.value = settings
    }
}

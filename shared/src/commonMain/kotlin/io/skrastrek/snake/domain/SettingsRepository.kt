package io.skrastrek.snake.domain

import io.skrastrek.snake.domain.model.GameSettings
import kotlinx.coroutines.flow.Flow

/**
 * Persists and observes the player's [GameSettings] (currently the speed tier).
 *
 * In-memory today ([io.skrastrek.snake.data.InMemorySettingsRepository]); a
 * SQLDelight/preferences-backed implementation can drop in without touching callers.
 */
interface SettingsRepository {
    /** Emits the current settings, then again whenever they change. */
    fun settings(): Flow<GameSettings>

    /** Replace the stored settings. */
    suspend fun update(settings: GameSettings)
}

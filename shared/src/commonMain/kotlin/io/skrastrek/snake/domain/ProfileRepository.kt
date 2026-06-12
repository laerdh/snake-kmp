package io.skrastrek.snake.domain

import io.skrastrek.snake.domain.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

/**
 * Observes the player's [PlayerProfile] shown on the Settings screen.
 *
 * In-memory today ([io.skrastrek.snake.data.InMemoryProfileRepository]); a remote
 * or local-store backing can drop in without touching callers.
 */
interface ProfileRepository {
    /** Emits the current profile, then again whenever it changes. */
    fun profile(): Flow<PlayerProfile>
}

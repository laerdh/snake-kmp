package io.skrastrek.snake.data

import io.skrastrek.snake.domain.ProfileRepository
import io.skrastrek.snake.domain.model.PlayerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory [ProfileRepository], seeded with [PlayerProfile.Default]. The design
 * surfaces the profile read-only, so there is no mutating API yet.
 */
class InMemoryProfileRepository : ProfileRepository {
    private val state = MutableStateFlow(PlayerProfile.Default)

    override fun profile(): Flow<PlayerProfile> = state.asStateFlow()
}

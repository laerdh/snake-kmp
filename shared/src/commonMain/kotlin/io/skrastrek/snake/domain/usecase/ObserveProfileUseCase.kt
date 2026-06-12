package io.skrastrek.snake.domain.usecase

import io.skrastrek.snake.domain.ProfileRepository
import io.skrastrek.snake.domain.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

/** Streams the player's [PlayerProfile]. */
class ObserveProfileUseCase(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<PlayerProfile> = repository.profile()
}

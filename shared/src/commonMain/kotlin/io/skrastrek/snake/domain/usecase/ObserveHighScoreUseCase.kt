package io.skrastrek.snake.domain.usecase

import io.skrastrek.snake.domain.HighScoreRepository
import kotlinx.coroutines.flow.Flow

/** Streams the player's current best score. */
class ObserveHighScoreUseCase(
    private val repository: HighScoreRepository,
) {
    operator fun invoke(): Flow<Int> = repository.highScore()
}

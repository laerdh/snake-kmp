package io.skrastrek.snake.domain.usecase

import io.skrastrek.snake.domain.ScoreRepository

/** Clears all recorded scores (the Settings "RESET PROGRESS" action). */
class ResetProgressUseCase(
    private val repository: ScoreRepository,
) {
    suspend operator fun invoke() = repository.reset()
}

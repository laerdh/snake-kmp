package io.skrastrek.snake.domain.usecase

import io.skrastrek.snake.domain.HighScoreRepository

/** Submits a finished round's score, returning `true` if it set a new record. */
class SubmitScoreUseCase(
    private val repository: HighScoreRepository,
) {
    suspend operator fun invoke(score: Int): Boolean = repository.submitScore(score)
}

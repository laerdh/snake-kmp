package io.skrastrek.snake.domain.usecase

import io.skrastrek.snake.domain.ScoreRepository
import kotlinx.coroutines.flow.Flow

/** Streams the Start-screen leaderboard (best scores, highest first). */
class ObserveRecentScoresUseCase(
    private val repository: ScoreRepository,
) {
    operator fun invoke(): Flow<List<Int>> = repository.recentScores()
}

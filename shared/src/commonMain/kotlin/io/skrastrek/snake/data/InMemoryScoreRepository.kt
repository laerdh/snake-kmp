package io.skrastrek.snake.data

import io.skrastrek.snake.domain.ScoreRepository
import io.skrastrek.snake.domain.ScoreRepository.Companion.LEADERBOARD_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory [ScoreRepository]. Scores survive for the lifetime of the process
 * only. Replace with a SQLDelight-backed source for durable persistence.
 *
 * The full history is kept internally and exposed as a descending top-N
 * leaderboard, so [highScore] is simply its first entry.
 */
class InMemoryScoreRepository : ScoreRepository {
    private val scores = MutableStateFlow<List<Int>>(emptyList())

    private val leaderboard: Flow<List<Int>> =
        scores.map { all -> all.sortedDescending().take(LEADERBOARD_SIZE) }

    override fun recentScores(): Flow<List<Int>> = leaderboard

    override fun highScore(): Flow<Int> =
        scores.map { all -> all.maxOrNull() ?: 0 }

    override suspend fun submitScore(score: Int): Boolean {
        val previousBest = scores.value.maxOrNull() ?: 0
        scores.update { it + score }
        return score > previousBest
    }

    override suspend fun reset() {
        scores.update { emptyList() }
    }
}

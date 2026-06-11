package io.skrastrek.snake.data

import io.skrastrek.snake.domain.HighScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory [HighScoreRepository]. The best score survives for the lifetime of
 * the process only. Replace with a SQLDelight-backed source for persistence.
 */
class InMemoryHighScoreRepository : HighScoreRepository {
    private val best = MutableStateFlow(0)

    override fun highScore(): Flow<Int> = best.asStateFlow()

    override suspend fun submitScore(score: Int): Boolean {
        var improved = false
        best.update { current ->
            if (score > current) {
                improved = true
                score
            } else {
                current
            }
        }
        return improved
    }
}

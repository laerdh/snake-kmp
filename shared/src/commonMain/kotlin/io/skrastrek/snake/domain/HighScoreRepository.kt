package io.skrastrek.snake.domain

import kotlinx.coroutines.flow.Flow

/**
 * Persists and observes the player's best score.
 *
 * The current implementation is in-memory ([io.skrastrek.snake.data.InMemoryHighScoreRepository]);
 * swapping in a SQLDelight-backed implementation requires no changes to callers.
 */
interface HighScoreRepository {
    /** Emits the current high score, then again whenever it changes. */
    fun highScore(): Flow<Int>

    /**
     * Record [score] if it beats the stored best.
     *
     * @return `true` if a new high score was set.
     */
    suspend fun submitScore(score: Int): Boolean
}

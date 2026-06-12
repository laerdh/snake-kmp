package io.skrastrek.snake.domain

import kotlinx.coroutines.flow.Flow

/**
 * Persists and observes the player's scores: the leaderboard shown on the Start
 * screen and the best score shown during play.
 *
 * The current implementation is in-memory ([io.skrastrek.snake.data.InMemoryScoreRepository]);
 * swapping in a SQLDelight-backed implementation requires no changes to callers.
 */
interface ScoreRepository {
    /** Emits the leaderboard — best scores, highest first, capped at [LEADERBOARD_SIZE]. */
    fun recentScores(): Flow<List<Int>>

    /** Emits the current best score (`0` when none recorded), then again on change. */
    fun highScore(): Flow<Int>

    /**
     * Record a finished round's [score].
     *
     * @return `true` if it set a new best.
     */
    suspend fun submitScore(score: Int): Boolean

    /** Clear all recorded scores (the Settings "RESET PROGRESS" action). */
    suspend fun reset()

    companion object {
        /** How many scores the Start-screen leaderboard shows. */
        const val LEADERBOARD_SIZE = 5
    }
}

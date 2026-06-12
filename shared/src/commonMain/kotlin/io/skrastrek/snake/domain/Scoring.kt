package io.skrastrek.snake.domain

import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.SnakeGame

/**
 * The two-part result of a round, as shown on the scoreboard and Game Over screen.
 *
 * @property points the headline "TOTAL SCORE" — apples scaled by the speed tier.
 * @property applesEaten raw food count.
 * @property elapsedMillis survival time, in milliseconds.
 */
data class RoundScore(
    val points: Int,
    val applesEaten: Int,
    val elapsedMillis: Long,
)

/**
 * Derives the displayed [RoundScore] for this snapshot at the given [difficulty].
 * Points scale with [Difficulty.pointsPerApple], so the same apple count is worth
 * more on a faster tier.
 */
fun SnakeGame.scoreWith(difficulty: Difficulty): RoundScore = RoundScore(
    points = applesEaten * difficulty.pointsPerApple,
    applesEaten = applesEaten,
    elapsedMillis = elapsedMillis,
)

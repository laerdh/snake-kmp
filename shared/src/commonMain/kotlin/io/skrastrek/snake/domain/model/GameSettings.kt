package io.skrastrek.snake.domain.model

/**
 * Player-adjustable game configuration, edited on the Settings screen.
 *
 * @property difficulty selected speed tier (GAME SPEED slider).
 */
data class GameSettings(
    val difficulty: Difficulty,
) {
    companion object {
        /** The configuration a fresh install starts with. */
        val Default: GameSettings = GameSettings(difficulty = Difficulty.Default)
    }
}

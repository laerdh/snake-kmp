package io.skrastrek.snake.domain.model

/** Lifecycle phase of a single round of Snake. */
enum class GameStatus {
    /** Board is set up, waiting for the player's first move. */
    Ready,

    /** Snake is advancing automatically each tick. */
    Running,

    /** Round paused by the player; resumable. */
    Paused,

    /** Snake collided with a wall or itself; round is finished. */
    GameOver,
}

/**
 * Immutable snapshot of a Snake round. Produced exclusively by the game rules in
 * [io.skrastrek.snake.domain.GameEngine]; never mutated in place.
 *
 * @property snake the snake's body cells, head first. Always non-empty while playing.
 * @property food current food cell, or `null` if the board is momentarily full.
 * @property direction the direction the head will move on the next tick.
 * @property status current [GameStatus].
 * @property score food eaten this round.
 * @property gridWidth number of columns on the board.
 * @property gridHeight number of rows on the board.
 */
data class SnakeGame(
    val snake: List<GridPoint>,
    val food: GridPoint?,
    val direction: Direction,
    val status: GameStatus,
    val score: Int,
    val gridWidth: Int,
    val gridHeight: Int,
) {
    /** The snake's head cell. */
    val head: GridPoint get() = snake.first()
}

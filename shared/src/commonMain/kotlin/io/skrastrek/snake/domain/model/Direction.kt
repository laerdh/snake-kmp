package io.skrastrek.snake.domain.model

/**
 * The four directions the snake can travel, with the unit delta applied to the
 * head on each game tick.
 *
 * @property dx horizontal step (`-1`, `0` or `1`).
 * @property dy vertical step (`-1`, `0` or `1`).
 */
enum class Direction(val dx: Int, val dy: Int) {
    Up(0, -1),
    Down(0, 1),
    Left(-1, 0),
    Right(1, 0);

    /** The directly opposing direction. A snake may never reverse onto itself. */
    val opposite: Direction
        get() = when (this) {
            Up -> Down
            Down -> Up
            Left -> Right
            Right -> Left
        }

    /** Whether [other] is on the same axis (horizontal or vertical) as this. */
    fun isAxisAlignedWith(other: Direction): Boolean =
        this == other || this == other.opposite
}

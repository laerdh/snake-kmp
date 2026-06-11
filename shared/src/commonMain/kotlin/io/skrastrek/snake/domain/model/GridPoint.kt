package io.skrastrek.snake.domain.model

/**
 * A single cell coordinate on the game grid.
 *
 * @property x column index, `0` is the left edge.
 * @property y row index, `0` is the top edge.
 */
data class GridPoint(val x: Int, val y: Int)

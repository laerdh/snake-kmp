package io.skrastrek.snake.domain

import io.skrastrek.snake.domain.model.Direction
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.model.GridPoint
import io.skrastrek.snake.domain.model.SnakeGame
import kotlin.random.Random

/**
 * Pure, framework-free rules of Snake. Every method returns a new [SnakeGame] —
 * state is never mutated in place — which keeps the engine trivially testable
 * and safe to drive from a Circuit Presenter.
 *
 * Randomness (food placement) is injected via [random] so tests can seed it for
 * deterministic results.
 *
 * @property gridWidth number of columns on the board.
 * @property gridHeight number of rows on the board.
 * @property random source of randomness for food placement.
 */
class GameEngine(
    private val gridWidth: Int = DEFAULT_GRID_SIZE,
    private val gridHeight: Int = DEFAULT_GRID_SIZE,
    private val random: Random = Random.Default,
) {
    /**
     * A fresh round with the snake centred, [INITIAL_LENGTH] cells long, facing
     * [Direction.Right], in the [GameStatus.Ready] state.
     */
    fun newGame(): SnakeGame {
        val centerY = gridHeight / 2
        val startX = gridWidth / 2
        // Head first; body trails to the left so the snake faces right.
        val snake = (0 until INITIAL_LENGTH).map { offset ->
            GridPoint(x = startX - offset, y = centerY)
        }
        return SnakeGame(
            snake = snake,
            food = spawnFood(snake),
            direction = Direction.Right,
            status = GameStatus.Ready,
            applesEaten = 0,
            elapsedMillis = 0L,
            gridWidth = gridWidth,
            gridHeight = gridHeight,
        )
    }

    /**
     * Apply a requested [direction] change. Ignored if the game is over, or if it
     * would reverse the snake directly onto its own neck.
     */
    fun changeDirection(game: SnakeGame, direction: Direction): SnakeGame {
        if (game.status == GameStatus.GameOver) return game
        if (direction == game.direction.opposite) return game
        // First input on a Ready board also starts the round.
        val status = if (game.status == GameStatus.Ready) GameStatus.Running else game.status
        return game.copy(direction = direction, status = status)
    }

    /**
     * Advance the simulation by one step. Only has an effect while
     * [GameStatus.Running]; otherwise the game is returned unchanged.
     *
     * @param stepMillis wall-clock duration this step represents (the active
     *   [io.skrastrek.snake.domain.model.Difficulty.tickMillis]); accumulated into
     *   [SnakeGame.elapsedMillis] so survival time can be reported at game over.
     */
    fun tick(game: SnakeGame, stepMillis: Long = 0L): SnakeGame {
        if (game.status != GameStatus.Running) return game

        val elapsed = game.elapsedMillis + stepMillis
        val newHead = GridPoint(
            x = game.head.x + game.direction.dx,
            y = game.head.y + game.direction.dy,
        )

        if (isOutOfBounds(newHead) || isSelfCollision(newHead, game.snake)) {
            return game.copy(status = GameStatus.GameOver, elapsedMillis = elapsed)
        }

        val ate = newHead == game.food
        // Grow by keeping the tail when eating; otherwise drop the last cell.
        val body = if (ate) game.snake else game.snake.dropLast(1)
        val newSnake = buildList {
            add(newHead)
            addAll(body)
        }

        return game.copy(
            snake = newSnake,
            food = if (ate) spawnFood(newSnake) else game.food,
            applesEaten = if (ate) game.applesEaten + 1 else game.applesEaten,
            elapsedMillis = elapsed,
        )
    }

    /** Toggle between [GameStatus.Running] and [GameStatus.Paused]. No-op otherwise. */
    fun togglePause(game: SnakeGame): SnakeGame = when (game.status) {
        GameStatus.Running -> game.copy(status = GameStatus.Paused)
        GameStatus.Paused -> game.copy(status = GameStatus.Running)
        else -> game
    }

    private fun isOutOfBounds(p: GridPoint): Boolean =
        p.x < 0 || p.y < 0 || p.x >= gridWidth || p.y >= gridHeight

    private fun isSelfCollision(head: GridPoint, snake: List<GridPoint>): Boolean {
        // The tail cell vacates this tick, so a head landing on it is allowed.
        val occupied = snake.dropLast(1)
        return head in occupied
    }

    /** Pick a uniformly-random free cell for food, or `null` if the board is full. */
    private fun spawnFood(snake: List<GridPoint>): GridPoint? {
        val occupied = snake.toHashSet()
        val free = ArrayList<GridPoint>(gridWidth * gridHeight)
        for (y in 0 until gridHeight) {
            for (x in 0 until gridWidth) {
                val cell = GridPoint(x, y)
                if (cell !in occupied) free.add(cell)
            }
        }
        return if (free.isEmpty()) null else free[random.nextInt(free.size)]
    }

    companion object {
        /** Default square arena (1:1 aspect ratio per the design spec). */
        const val DEFAULT_GRID_SIZE = 17

        /** Starting snake length, in cells. */
        const val INITIAL_LENGTH = 3
    }
}

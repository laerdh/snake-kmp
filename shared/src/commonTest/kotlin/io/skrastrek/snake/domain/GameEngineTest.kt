package io.skrastrek.snake.domain

import io.skrastrek.snake.domain.model.Direction
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.model.GridPoint
import io.skrastrek.snake.domain.model.SnakeGame
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameEngineTest {

    private val engine = GameEngine(gridWidth = 10, gridHeight = 10)

    private fun running(
        snake: List<GridPoint>,
        direction: Direction,
        food: GridPoint?,
        applesEaten: Int = 0,
        elapsedMillis: Long = 0L,
    ) = SnakeGame(
        snake = snake,
        food = food,
        direction = direction,
        status = GameStatus.Running,
        applesEaten = applesEaten,
        elapsedMillis = elapsedMillis,
        gridWidth = 10,
        gridHeight = 10,
    )

    @Test
    fun newGame_startsReadyWithInitialSnake() {
        val game = engine.newGame()
        assertEquals(GameStatus.Ready, game.status)
        assertEquals(0, game.applesEaten)
        assertEquals(0L, game.elapsedMillis)
        assertEquals(GameEngine.INITIAL_LENGTH, game.snake.size)
        assertNotNull(game.food)
        assertTrue(game.food !in game.snake, "food must not spawn on the snake")
    }

    @Test
    fun changeDirection_fromReady_startsRunning() {
        val started = engine.changeDirection(engine.newGame(), Direction.Up)
        assertEquals(GameStatus.Running, started.status)
        assertEquals(Direction.Up, started.direction)
    }

    @Test
    fun changeDirection_ignoresReversal() {
        val game = running(listOf(GridPoint(5, 5), GridPoint(4, 5)), Direction.Right, food = null)
        val result = engine.changeDirection(game, Direction.Left)
        assertEquals(Direction.Right, result.direction, "must not reverse onto its own neck")
    }

    @Test
    fun tick_movesAndKeepsLengthWhenNotEating() {
        val game = running(
            snake = listOf(GridPoint(5, 5), GridPoint(4, 5), GridPoint(3, 5)),
            direction = Direction.Right,
            food = GridPoint(0, 0),
        )
        val next = engine.tick(game, stepMillis = 110L)
        assertEquals(GridPoint(6, 5), next.head)
        assertEquals(3, next.snake.size)
        assertEquals(0, next.applesEaten)
        assertEquals(110L, next.elapsedMillis, "each step accumulates survival time")
    }

    @Test
    fun tick_growsAndScoresWhenEating() {
        val game = running(
            snake = listOf(GridPoint(5, 5), GridPoint(4, 5), GridPoint(3, 5)),
            direction = Direction.Right,
            food = GridPoint(6, 5),
        )
        val next = engine.tick(game)
        assertEquals(GridPoint(6, 5), next.head)
        assertEquals(4, next.snake.size, "snake grows by one when eating")
        assertEquals(1, next.applesEaten)
        assertNotNull(next.food, "a new food spawns after eating")
    }

    @Test
    fun tick_intoWall_endsGame() {
        val game = running(listOf(GridPoint(9, 5), GridPoint(8, 5)), Direction.Right, food = null)
        assertEquals(GameStatus.GameOver, engine.tick(game).status)
    }

    @Test
    fun tick_intoSelf_endsGame() {
        // 2x2 loop; moving Up sends the head onto an occupied (non-tail) cell.
        val game = running(
            snake = listOf(GridPoint(1, 1), GridPoint(1, 0), GridPoint(0, 0), GridPoint(0, 1)),
            direction = Direction.Up,
            food = null,
        )
        assertEquals(GameStatus.GameOver, engine.tick(game).status)
    }

    @Test
    fun togglePause_flipsRunningAndPaused() {
        val game = running(listOf(GridPoint(5, 5)), Direction.Right, food = null)
        val paused = engine.togglePause(game)
        assertEquals(GameStatus.Paused, paused.status)
        assertEquals(GameStatus.Running, engine.togglePause(paused).status)
    }
}

package io.skrastrek.snake.ui.game

import com.slack.circuit.test.test
import io.skrastrek.snake.data.InMemoryHighScoreRepository
import io.skrastrek.snake.domain.GameEngine
import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.Direction
import io.skrastrek.snake.domain.model.GameStatus
import io.skrastrek.snake.domain.usecase.ObserveHighScoreUseCase
import io.skrastrek.snake.domain.usecase.SubmitScoreUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GamePresenterTest {

    private fun presenter(): GamePresenter {
        val repository = InMemoryHighScoreRepository()
        return GamePresenter(
            engine = GameEngine(gridWidth = 10, gridHeight = 10),
            observeHighScore = ObserveHighScoreUseCase(repository),
            submitScore = SubmitScoreUseCase(repository),
        )
    }

    @Test
    fun emitsReadyBoardInitially() = runTest {
        presenter().test {
            val state = awaitItem()
            assertEquals(GameStatus.Ready, state.game.status)
            assertEquals(0, state.game.score)
            assertEquals(Difficulty.Normal, state.difficulty)
            assertEquals(GameEngine.INITIAL_LENGTH, state.game.snake.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun directionEventStartsRound() = runTest {
        presenter().test {
            val ready = awaitItem()
            assertEquals(GameStatus.Ready, ready.game.status)

            ready.eventSink(GameUiEvent.DirectionChanged(Direction.Down))

            val running = awaitItem()
            assertEquals(GameStatus.Running, running.game.status)
            assertEquals(Direction.Down, running.game.direction)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

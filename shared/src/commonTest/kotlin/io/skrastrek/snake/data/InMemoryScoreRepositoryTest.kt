package io.skrastrek.snake.data

import app.cash.turbine.test
import io.skrastrek.snake.domain.ScoreRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InMemoryScoreRepositoryTest {

    @Test
    fun leaderboardIsDescendingAndCapped() = runTest {
        val repo = InMemoryScoreRepository()
        listOf(100, 500, 200, 900, 50, 700).forEach { repo.submitScore(it) }

        repo.recentScores().test {
            val top = awaitItem()
            assertEquals(listOf(900, 700, 500, 200, 100), top)
            assertEquals(ScoreRepository.LEADERBOARD_SIZE, top.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitReportsNewBestOnlyWhenItBeatsThePrevious() = runTest {
        val repo = InMemoryScoreRepository()
        assertTrue(repo.submitScore(100), "first score is always a new best")
        assertFalse(repo.submitScore(80), "lower score is not a new best")
        assertTrue(repo.submitScore(150), "higher score is a new best")
    }

    @Test
    fun resetClearsAllScores() = runTest {
        val repo = InMemoryScoreRepository()
        repo.submitScore(300)
        repo.reset()

        repo.highScore().test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        repo.recentScores().test {
            assertEquals(emptyList(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

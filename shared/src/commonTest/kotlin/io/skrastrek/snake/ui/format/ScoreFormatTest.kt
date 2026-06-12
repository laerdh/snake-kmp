package io.skrastrek.snake.ui.format

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreFormatTest {

    @Test
    fun groupsThousandsWithCommas() {
        assertEquals("0", 0.toGroupedScore())
        assertEquals("999", 999.toGroupedScore())
        assertEquals("1,000", 1_000.toGroupedScore())
        assertEquals("14,250", 14_250.toGroupedScore())
        assertEquals("1,000,000", 1_000_000.toGroupedScore())
    }

    @Test
    fun padsScoreToWidth() {
        assertEquals("0000", 0.toPaddedScore())
        assertEquals("0256", 256.toPaddedScore())
        assertEquals("10000", 10_000.toPaddedScore())
    }

    @Test
    fun formatsSurvivalTimeAsMinutesSeconds() {
        assertEquals("00:00", 0L.toSurvivalTime())
        assertEquals("00:09", 9_000L.toSurvivalTime())
        assertEquals("03:14", 194_000L.toSurvivalTime())
        assertEquals("10:00", 600_000L.toSurvivalTime())
    }

    @Test
    fun clampsNegativesToZero() {
        assertEquals("0", (-5).toGroupedScore())
        assertEquals("0000", (-5).toPaddedScore())
        assertEquals("00:00", (-5L).toSurvivalTime())
    }
}

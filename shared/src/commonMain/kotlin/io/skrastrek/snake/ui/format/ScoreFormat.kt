package io.skrastrek.snake.ui.format

/**
 * Presentation-layer formatting for scores and timers. Kept off the domain models
 * so the same figures can be rendered differently per screen (zero-padded on the
 * live scoreboard, comma-grouped on the leaderboard, `MM:SS` for survival time).
 */

private const val GROUP_SIZE = 3
private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_SECOND = 1000

/** Groups thousands with commas, e.g. `14250` → `"14,250"`. Negatives are clamped to 0. */
fun Int.toGroupedScore(): String {
    val digits = coerceAtLeast(0).toString()
    return buildString {
        digits.forEachIndexed { index, c ->
            if (index > 0 && (digits.length - index) % GROUP_SIZE == 0) append(',')
            append(c)
        }
    }
}

/** Zero-pads to at least [width] digits, e.g. `256` → `"0256"`. Negatives are clamped to 0. */
fun Int.toPaddedScore(width: Int = 4): String =
    coerceAtLeast(0).toString().padStart(width, '0')

/** Formats a duration in millis as `MM:SS`, e.g. `194_000` → `"03:14"`. */
fun Long.toSurvivalTime(): String {
    val totalSeconds = (coerceAtLeast(0L) / MILLIS_PER_SECOND).toInt()
    val minutes = totalSeconds / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

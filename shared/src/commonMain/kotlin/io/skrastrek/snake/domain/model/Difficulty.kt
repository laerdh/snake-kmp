package io.skrastrek.snake.domain.model

/**
 * Game difficulty, expressed as the delay between game ticks. Lower delay means
 * a faster snake. Surfaced in the UI as the pill-shaped difficulty chips.
 *
 * @property label short display label (e.g. shown on a chip).
 * @property tickMillis delay between automatic snake advances.
 */
enum class Difficulty(val label: String, val tickMillis: Long) {
    Easy("EASY", 200L),
    Normal("NORMAL", 130L),
    Hard("HARD", 85L),
    Insane("INSANE", 55L),
}

package io.skrastrek.snake.domain.model

/**
 * Game speed tier, chosen via the GAME SPEED slider on the Settings screen.
 *
 * A faster snake ([tickMillis] lower) is harder, so it also rewards more
 * [pointsPerApple]. The three tiers map 1:1 onto the slider's 0..2 range via
 * [sliderIndex] / [fromSliderIndex].
 *
 * @property displayName human-readable label shown on the slider (e.g. "Chill").
 * @property tickMillis delay between automatic snake advances; lower is faster.
 * @property pointsPerApple score awarded for each apple eaten at this tier.
 */
enum class Difficulty(
    val displayName: String,
    val tickMillis: Long,
    val pointsPerApple: Int,
) {
    Chill(displayName = "Chill", tickMillis = 200L, pointsPerApple = 10),
    Fast(displayName = "Fast", tickMillis = 110L, pointsPerApple = 25),
    Insane(displayName = "Insane", tickMillis = 60L, pointsPerApple = 50);

    /** Position of this tier on the 0..2 GAME SPEED slider. */
    val sliderIndex: Int get() = ordinal

    companion object {
        /** The tier a fresh install starts on. */
        val Default: Difficulty = Chill

        /** The tier for a 0..2 slider position, clamped to the valid range. */
        fun fromSliderIndex(index: Int): Difficulty =
            entries.getOrElse(index) { Default }
    }
}

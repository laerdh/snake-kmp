package io.skrastrek.snake.domain.model

/**
 * The player's public identity, shown on the Settings screen profile card.
 *
 * @property name display handle (e.g. "Neo_Runner_99").
 * @property rank earned rank label (e.g. "Ranked Master").
 */
data class PlayerProfile(
    val name: String,
    val rank: String,
) {
    companion object {
        /** The identity a fresh install starts with. */
        val Default: PlayerProfile = PlayerProfile(name = "Neo_Runner_99", rank = "Ranked Master")
    }
}

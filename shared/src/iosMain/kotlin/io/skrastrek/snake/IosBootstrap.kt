package io.skrastrek.snake

import io.skrastrek.snake.di.initKoin

/**
 * Starts Koin for the iOS host. Called from `iOSApp.swift` at launch, before the
 * Compose [MainViewController] is created.
 *
 * Exposed as a no-argument function (rather than [initKoin], which has a default
 * lambda parameter that does not bridge cleanly to Swift).
 */
fun setupKoin() = initKoin()

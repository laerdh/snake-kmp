package io.skrastrek.snake.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/** All shared Koin modules, in dependency order. */
val sharedModules: List<Module> = listOf(dataModule, domainModule, circuitModule)

/**
 * Starts Koin with the shared modules plus any [platformDeclaration] supplied by
 * the host platform (e.g. Android needs `androidContext(...)`).
 *
 * Call this once at app startup — from `Application.onCreate` on Android and from
 * the Swift entry point on iOS, before any Compose content is created.
 */
fun initKoin(platformDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        platformDeclaration()
        modules(sharedModules)
    }
}

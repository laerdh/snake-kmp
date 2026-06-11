package io.skrastrek.snake.platform

/**
 * Multiplatform stand-in for `kotlinx.parcelize.Parcelize`.
 *
 * Circuit's [com.slack.circuit.runtime.screen.Screen] is `Parcelable` on Android
 * (so the navigation backstack can be saved/restored), but has no such
 * requirement on iOS.
 *
 * Under the K2 compiler a `typealias` to `@Parcelize` no longer triggers the
 * parcelize plugin (see KT-58892). Instead this is a plain annotation that the
 * Android compilation registers with the plugin via the `additionalAnnotation`
 * compiler argument (see `shared/build.gradle.kts`). On iOS the annotation is
 * simply inert, since the parcelize plugin does not run for native targets.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class CommonParcelize

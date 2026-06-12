package io.skrastrek.snake

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * iOS entry point into the shared Compose UI. Returned to Swift and embedded in
 * a SwiftUI `UIViewControllerRepresentable`.
 *
 * Koin must already be started (see `iOSApp.swift`) before this is invoked.
 */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }

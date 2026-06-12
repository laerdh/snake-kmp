# snake-kmp

A Neo-Retro Arcade **Snake** game built with **Kotlin Multiplatform** + **Compose Multiplatform**, sharing all UI, presentation and game logic between **Android** and **iOS**.

The architecture follows the conventions in [`CLAUDE.md`](CLAUDE.md): clean architecture with **Circuit** (Slack) for state + navigation, **Koin** for DI, and design tokens from [`DESIGN.md`](DESIGN.md).

## Stack

| | Version |
|---|---|
| Kotlin | 2.4.0 |
| Compose Multiplatform | 1.11.0 |
| Circuit | 0.34.0 |
| Koin | 4.1.1 |
| Android Gradle Plugin | 8.11.1 |
| Gradle | 8.14.3 |

All versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Project layout

```
shared/                     # All shared code (KMP)
  commonMain/kotlin/io/skrastrek/snake/
    App.kt                  # Circuit root (backstack + navigator)
    domain/                 # GameEngine (pure rules), models, use cases, repo interface
    data/                   # InMemoryHighScoreRepository
    ui/theme/               # AppColors / AppTypography / AppDimensions / SnakeTheme (from DESIGN.md)
    ui/game/                # GameScreen: Screen + State + Event + Presenter + Ui + Circuit factories
    di/                     # Koin modules (data, domain, circuit) + initKoin
    platform/               # @CommonParcelize (KMP Parcelable shim)
  androidMain/              # @Preview + Android @CommonParcelize wiring
  iosMain/                  # MainViewController() + setupKoin() for Swift
  commonTest/               # GameEngine + Circuit Presenter tests
androidApp/                 # Android entry point (Application + MainActivity)
iosApp/                     # Xcode project + SwiftUI entry point
```

## Build & run

### Android
```bash
./gradlew :androidApp:assembleDebug      # build debug APK
./gradlew :androidApp:installDebug       # install on a running emulator/device
```

### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator/device, or from the CLI:
```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Debug -sdk iphonesimulator \
  -destination 'platform=iOS Simulator,name=iPhone 15' build
```
The Xcode **Compile Kotlin Framework** build phase runs `:shared:embedAndSignAppleFrameworkForXcode` automatically.

### Tests
```bash
./gradlew :shared:testDebugUnitTest        # JVM/Android
./gradlew :shared:iosSimulatorArm64Test    # iOS (Apple Silicon)
```

## How it plays

Swipe on the board or use the on-screen D-pad to steer. The centre button pauses/resumes. Pick a difficulty chip (Easy → Insane) to set the speed; changing it restarts the round. Eating food grows the snake and scores a point; hitting a wall or yourself ends the round, and your best score is tracked for the session.

## Notes

- **Parcelable on KMP:** under the K2 compiler a `typealias` to `@Parcelize` no longer triggers the plugin ([KT-58892](https://youtrack.jetbrains.com/issue/KT-58892)). `@CommonParcelize` is therefore a plain `commonMain` annotation registered with the parcelize plugin via the `additionalAnnotation` compiler arg in `shared/build.gradle.kts`.
- The brand fonts (Sora, JetBrains Mono) are not yet bundled — `AppTypography` falls back to system sans/monospace families. Drop the font files under `commonMain/composeResources/font/` and swap the families to ship them.
- High scores are in-memory only; the `HighScoreRepository` interface is ready for a SQLDelight-backed implementation.

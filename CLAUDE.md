# Project — Compose Multiplatform (Android & iOS)

## Overview
This is a Kotlin Multiplatform project using Compose Multiplatform for shared UI,
targeting Android and iOS. Business logic, UI, and data layers are all shared via
the `shared` module. Platform-specific code is kept to an absolute minimum.

---

## Project structure

```
project-root/
├── shared/
│   ├── src/
│   │   ├── commonMain/kotlin/       # Shared code: UI, domain, data
│   │   │   ├── ui/                  # Compose Multiplatform screens and components
│   │   │   ├── domain/              # Use cases, domain models, repository interfaces
│   │   │   ├── data/                # Repository implementations, data sources, DTOs
│   │   │   └── di/                  # Koin modules
│   │   ├── androidMain/kotlin/      # Android expect/actual implementations only
│   │   ├── iosMain/kotlin/          # iOS expect/actual implementations only
│   │   ├── commonTest/kotlin/       # Shared unit tests
│   │   ├── androidUnitTest/kotlin/  # Android-specific unit tests
│   │   └── iosTest/kotlin/          # iOS-specific unit tests
├── androidApp/
│   ├── src/main/kotlin/             # Android entry point (MainActivity, Application)
│   └── src/main/res/                # Android resources (strings, drawables, etc.)
├── iosApp/
│   ├── iosApp.xcodeproj/
│   └── iosApp/                      # iOS entry point (SwiftUI App, ContentView)
├── gradle/
│   └── libs.versions.toml           # Version catalog — single source of truth
├── CLAUDE.md                        # This file
└── DESIGN.md                        # Design tokens (if exported from Stitch/Figma)
```

---

## Architecture

### Pattern
Clean architecture with three layers — all in `commonMain` unless platform-specific.
**Circuit by Slack** is used for state management and UI architecture throughout.

```
UI layer         →  Compose Multiplatform screens (Circuit Ui)
                     Circuit Presenter (holds state, handles events)
                     
Domain layer     →  Use cases (one public function each)
                     Domain models (pure Kotlin data classes)
                     Repository interfaces
                     
Data layer       →  Repository implementations
                     Remote data sources (Ktor)
                     Local data sources (SQLDelight)
                     DTOs + mappers to domain models
```

### Circuit — core concepts
Circuit enforces a strict unidirectional data flow. Every screen has three parts:

```kotlin
// 1. Screen — identifies the destination, holds input args (must be Parcelable)
@Parcelize
data class ShipmentDetailScreen(val shipmentId: String) : Screen

// 2. State — immutable snapshot of what the UI renders (sealed interface per screen)
sealed interface ShipmentDetailState : CircuitUiState {
    data object Loading : ShipmentDetailState
    data class Success(
        val shipment: Shipment,
        val eventSink: (ShipmentDetailEvent) -> Unit  // always last property
    ) : ShipmentDetailState
    data class Error(val message: String) : ShipmentDetailState
}

// 3. Event — user actions the UI sends back to the Presenter (sealed interface)
sealed interface ShipmentDetailEvent : CircuitUiEvent {
    data object RetryClicked : ShipmentDetailEvent
    data class StatusFilterChanged(val filter: StatusFilter) : ShipmentDetailEvent
}
```

### Presenter
```kotlin
class ShipmentDetailPresenter(
    private val screen: ShipmentDetailScreen,
    private val getShipmentUseCase: GetShipmentUseCase,
) : Presenter<ShipmentDetailState> {

    @Composable
    override fun present(): ShipmentDetailState {
        var shipment by remember { mutableStateOf<Shipment?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(screen.shipmentId) {
            shipment = getShipmentUseCase(screen.shipmentId)
            isLoading = false
        }

        return when {
            isLoading -> ShipmentDetailState.Loading
            shipment != null -> ShipmentDetailState.Success(
                shipment = shipment!!,
                eventSink = { event ->
                    when (event) {
                        is ShipmentDetailEvent.RetryClicked -> { /* handle */ }
                        is ShipmentDetailEvent.StatusFilterChanged -> { /* handle */ }
                    }
                }
            )
            else -> ShipmentDetailState.Error("Failed to load shipment")
        }
    }
}
```

### UI (Circuit Ui)
```kotlin
@Composable
fun ShipmentDetailUi(state: ShipmentDetailState, modifier: Modifier = Modifier) {
    when (state) {
        is ShipmentDetailState.Loading -> LoadingIndicator(modifier)
        is ShipmentDetailState.Error -> ErrorScreen(state.message, modifier)
        is ShipmentDetailState.Success -> ShipmentDetailContent(state, modifier)
    }
}

@Composable
private fun ShipmentDetailContent(state: ShipmentDetailState.Success, modifier: Modifier) {
    // UI only — no logic here. Send events via state.eventSink(...)
    Button(onClick = { state.eventSink(ShipmentDetailEvent.RetryClicked) }) {
        Text("Retry")
    }
}
```

### Navigation (Circuit Navigator)
- Use **Circuit's built-in `Navigator`** — do NOT use Decompose or Jetpack Navigation
- Navigate by pushing/popping `Screen` objects onto the Circuit backstack
- Use `rememberCircuitNavigator()` at the root and pass it down via Circuit's DI

```kotlin
// Navigating to a new screen
eventSink = { event ->
    when (event) {
        is ListEvent.ShipmentClicked ->
            navigator.goTo(ShipmentDetailScreen(event.shipmentId))
        is ListEvent.BackClicked -> navigator.pop()
    }
}
```

### State management rules
- All state lives in the Presenter — never in the UI composable
- State is always immutable — use `copy()` for updates, never mutate
- `eventSink` is always the **last property** in a Success state
- Never pass `Navigator` into a Presenter directly — use a `Screen` result callback pattern for return values
- Use `produceRetainedState` or `rememberRetained` for state that survives recomposition
- Side effects (one-shot navigation, toasts) belong in the Presenter via `LaunchedEffect`

---

## Key libraries

| Purpose              | Library                          | Notes                                      |
|----------------------|----------------------------------|--------------------------------------------|
| UI                   | Compose Multiplatform            | `org.jetbrains.compose`                    |
| State + navigation   | Circuit (Slack)                  | `com.slack.circuit:circuit-foundation`     |
| Circuit runtime      | Circuit KMP                      | `circuit-runtime`, `circuit-runtime-screen`|
| DI                   | Koin                             | `koin-core` + `koin-compose`               |
| Networking           | Ktor                             | `ktor-client-core` + platform engines      |
| Local DB             | SQLDelight                       | Generates type-safe Kotlin from SQL        |
| Serialization        | kotlinx.serialization            | Always use — no Gson or Moshi              |
| Async                | kotlinx.coroutines               | Flows and suspend functions only           |
| Date/Time            | kotlinx-datetime                 | No java.time in commonMain                 |
| Image loading        | Coil 3                           | KMP-compatible                             |
| Testing              | kotlin.test + Turbine            | Turbine for Flow testing                   |
| Circuit testing      | circuit-test                     | `FakeNavigator`, `Presenter.test {}`       |

---

## Coding conventions

### General
- All public APIs must have KDoc comments (classes, functions, properties)
- Use `data class` for domain models and DTOs
- Prefer `sealed interface` over `sealed class` for state and events
- Use `object` for singletons, not companion objects with `getInstance()`
- Never use `!!` — use `?: error(...)`, `requireNotNull()`, or safe handling
- No magic numbers or strings — use named constants or string resources

### Kotlin specifics
- Use named arguments for functions with more than two parameters
- Prefer extension functions over utility classes
- Use `value class` for domain primitives (e.g. `@JvmInline value class UserId(val value: String)`)
- Coroutine scopes: use `rememberCoroutineScope()` in Presenters for UI-scoped work, inject `CoroutineScope` in use cases

### Compose + Circuit UI conventions
- One screen per file — file contains `Screen`, `State`, `Event`, `Presenter`, and `Ui` function
- The `Ui` composable receives only `State` and `Modifier` — nothing else
- Never hoist state in the Composable itself — that is the Presenter's job
- All user interactions go through `state.eventSink(Event)` — no direct callbacks
- All Composables must have a `@Preview` in `androidMain` — pass a hardcoded `State.Success`
- Use `remember` and `derivedStateOf` only for derived UI state, not business state
- No `LaunchedEffect` in UI composables — side effects belong in the Presenter

### expect/actual
- Use `expect`/`actual` ONLY when there is no KMP-compatible alternative
- Acceptable cases: file system access, platform-specific SDKs, biometrics, camera
- Do NOT use `expect`/`actual` for: networking, serialization, date/time, logging
- Every `expect` declaration must have a KDoc explaining why it cannot be commonMain

---

## Dependency injection (Koin)

### Module structure
```kotlin
// shared/src/commonMain/kotlin/di/
├── domainModule.kt      # Use cases
├── dataModule.kt        # Repositories, data sources, Ktor client
├── presenterModule.kt   # Circuit Presenter factories
└── AppModule.kt         # Combines all modules, platform modules injected at startup
```

### Rules
- Never use `GlobalContext.get()` — always inject dependencies
- Register Circuit Presenter factories with `presenterFactoryOf<MyScreen, MyPresenter>()`
- Presenters are created by Circuit's runtime via factory — do not instantiate manually
- On iOS, initialize Koin in the Swift entry point before launching the Compose app
- Use `single { }` for repositories and data sources, `factory { }` for use cases and presenters

---

## Networking (Ktor)

```kotlin
// Always create the HttpClient in commonMain via a factory
// Platform engine is injected via expect/actual or Koin

val client = HttpClient(engine) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
    }
    defaultRequest {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }
}
```

- All API responses map to DTOs, then to domain models via mapper functions
- Use `Result<T>` or a custom `NetworkResult<T>` sealed class — never expose raw exceptions
- Base URLs and API keys come from environment/config, never hardcoded

---

## Local database (SQLDelight)

- `.sq` files live in `shared/src/commonMain/sqldelight/`
- Generate and commit schema files — do not regenerate on every build
- Database driver is provided via `expect`/`actual` (this is an accepted use case)
- Use `coroutines` extension for `Flow`-based queries

---

## Testing

### What to test
- All Presenters using `Presenter.test {}` from `circuit-test`
- All use cases (unit tests, fast, no Android framework)
- All repository implementations (with fake data sources)
- Mapper functions (domain ↔ DTO)

### Presenter testing pattern
```kotlin
class ShipmentDetailPresenterTest {
    @Test
    fun `emits loading then success state`() = runTest {
        val presenter = ShipmentDetailPresenter(
            screen = ShipmentDetailScreen("123"),
            getShipmentUseCase = FakeGetShipmentUseCase()
        )

        presenter.test {
            assertEquals(ShipmentDetailState.Loading, awaitItem())
            val success = awaitItem() as ShipmentDetailState.Success
            assertEquals("123", success.shipment.id)
        }
    }

    @Test
    fun `retry event reloads shipment`() = runTest {
        val presenter = ShipmentDetailPresenter(...)
        presenter.test {
            skipItems(2) // loading + success
            val state = awaitItem() as ShipmentDetailState.Success
            state.eventSink(ShipmentDetailEvent.RetryClicked)
            assertEquals(ShipmentDetailState.Loading, awaitItem())
        }
    }
}
```

### Navigation testing
- Use `FakeNavigator` from `circuit-test` to assert navigation events
- Never test navigation by inspecting Composable state — test via the Navigator

### Rules
- Tests live in `commonTest` unless they require platform APIs
- Use `runTest` for coroutine tests
- Use **Turbine** for testing raw `Flow` emissions outside of Circuit
- Use fake/stub implementations over mocks where possible
- Test file naming: `ClassNameTest.kt`

### Build commands
```bash
./gradlew build                              # Full build, all targets
./gradlew allTests                           # All unit tests
./gradlew :shared:testDebugUnitTest          # Android unit tests only
./gradlew :shared:iosSimulatorArm64Test      # iOS unit tests (Apple Silicon)
./gradlew :shared:iosX64Test                 # iOS unit tests (Intel Mac)
./gradlew :androidApp:assembleDebug          # Android debug APK
```

---

## Git conventions

### Branch naming
```
feature/TICKET-123-short-description
fix/TICKET-123-short-description
chore/update-dependencies
release/1.2.0
```

### Commit messages (Conventional Commits)
```
feat(auth): add biometric login support
fix(network): handle 401 token refresh correctly
chore(deps): update Ktor to 3.1.0
docs(api): add KDoc to ShipmentRepository
test(domain): add unit tests for TrackShipmentUseCase
```

### Rules
- Never commit directly to `main` or `develop`
- All changes go through a PR with at least one reviewer
- Squash commits on merge to keep history clean

---

## Version catalog (`gradle/libs.versions.toml`)

- This is the **single source of truth** for all dependency versions
- Never hardcode a version string in a `build.gradle.kts` file
- Always add new dependencies here first, then reference via `libs.xxx`
- Group related dependencies under a `[bundles]` entry when used together

---

## Do NOT

- Use `java.time.*` in `commonMain` — use `kotlinx-datetime`
- Use `java.io.File` in `commonMain` — use `expect`/`actual` or Okio
- Use `Thread.sleep()` — use `delay()` from coroutines
- Use `GlobalScope` — always use an injected or lifecycle-bound scope
- Use Gson or Moshi — use `kotlinx.serialization`
- Use Jetpack Navigation or Decompose — use Circuit's Navigator
- Use `ViewModel` — use Circuit Presenters
- Use `LiveData` — use Circuit state + `StateFlow`
- Use `SharedFlow` for events — use Circuit's `eventSink` pattern
- Put business logic or `LaunchedEffect` in UI composables — belongs in the Presenter
- Pass `Navigator` into a Presenter as a constructor dependency — use `goTo`/`pop` via the Circuit runtime
- Add Android-only dependencies to `commonMain`
- Modify `iosApp/` Swift/SwiftUI files without coordinating with the iOS team
- Force-push to any branch that has an open PR
- Merge your own PRs

---

## Design system

If `DESIGN.md` is present in the repo root, it is the source of truth for:
- Color tokens → implement in `shared/src/commonMain/ui/theme/AppColors.kt`
- Typography → `shared/src/commonMain/ui/theme/AppTypography.kt`
- Spacing/dimensions → `shared/src/commonMain/ui/theme/AppDimensions.kt`

All theme values must use the design tokens — no hardcoded `Color(0xFF...)` or `16.dp` inline.

---

## iOS notes

- The iOS app entry point is `iosApp/iosApp/iOSApp.swift`
- Koin must be initialized in Swift before `ComposeUIViewController` is created
- Do not add Swift business logic — all logic lives in shared Kotlin
- UI entry point: `MainViewController.kt` in `iosMain` returns a `UIViewController`
- Test on both simulator (arm64) and verify UI on physical device before PR

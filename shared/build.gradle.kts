import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            // K2 no longer triggers parcelize via a typealias (KT-58892), so we
            // register our plain commonMain @CommonParcelize annotation here.
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=io.skrastrek.snake.platform.CommonParcelize",
            )
        }
    }

    // iOS targets. The framework is consumed by the Xcode project under iosApp/.
    // (Circuit publishes iosArm64 + iosSimulatorArm64 only; iosX64 is dropped —
    // the Intel simulator is obsolete on Apple-Silicon hosts.)
    val iosTargets = listOf(iosArm64(), iosSimulatorArm64())
    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "shared"
            isStatic = true
            // Export Circuit's runtime so Swift can reference Screen types if needed.
            export(libs.circuit.runtime.screen)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose Multiplatform UI
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Circuit — state + navigation + UI architecture
            implementation(libs.bundles.circuit)
            api(libs.circuit.runtime.screen)

            // Dependency injection
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // kotlinx
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(compose.preview)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.circuit.test)
        }
    }
}

// Compose Multiplatform resources: bundled fonts live in
// `commonMain/composeResources/font/`. Pin the generated accessor class to a
// known package so call sites can `import io.skrastrek.snake.shared.resources.Res`.
compose.resources {
    publicResClass = true
    packageOfResClass = "io.skrastrek.snake.shared.resources"
    generateResClass = ResourcesExtension.ResourceClassGeneration.Always
}

android {
    namespace = "io.skrastrek.snake.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        // Circuit/Compose touch android.util.Log during composition; without this
        // the JVM unit-test stubs throw "Method not mocked".
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

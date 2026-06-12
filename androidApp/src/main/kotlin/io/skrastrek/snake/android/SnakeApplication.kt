package io.skrastrek.snake.android

import android.app.Application
import io.skrastrek.snake.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

/** Android [Application]. Starts Koin with the Android context before any UI. */
class SnakeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger(Level.ERROR)
            androidContext(this@SnakeApplication)
        }
    }
}

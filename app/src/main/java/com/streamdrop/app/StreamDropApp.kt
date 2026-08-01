package com.streamdrop.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * StreamDropApp
 *
 * Application class annotated with @HiltAndroidApp to initialize Hilt's
 * dependency injection component graph at application startup.
 */
@HiltAndroidApp
class StreamDropApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
    }
}

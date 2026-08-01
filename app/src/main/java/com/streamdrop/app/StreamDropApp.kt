package com.streamdrop.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * StreamDropApp
 *
 * Application class annotated with @HiltAndroidApp to initialize Hilt's
 * dependency injection component graph at application startup.
 *
 * Additional initialization (notification channels, Coil image loader,
 * WorkManager) will be added here in later stages.
 */
@HiltAndroidApp
class StreamDropApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Stage 2+: Register notification channels for download progress
        // Stage 2+: Initialize Coil with custom OkHttp client
    }
}

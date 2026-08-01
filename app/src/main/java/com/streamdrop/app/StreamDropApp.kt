package com.streamdrop.app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.streamdrop.app.core.util.DownloadDebugLog
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * StreamDropApp
 *
 * Application class annotated with @HiltAndroidApp to initialize Hilt's
 * dependency injection component graph and YoutubeDL engine at startup.
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
        installCrashLogger()
        try {
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
            DownloadDebugLog.i(this, "StreamDropApp", "YoutubeDL + FFmpeg init OK")
            
            // Check and update yt-dlp binary if required (7-day interval)
            kotlinx.coroutines.GlobalScope.launch {
                com.streamdrop.app.core.util.YtDlpUpdater.updateIfRequired(this@StreamDropApp)
            }
        } catch (e: YoutubeDLException) {
            DownloadDebugLog.e(this, "StreamDropApp", "Failed to initialize YoutubeDL engine", e)
        }
    }

    /** Captures process-killing exceptions that bypass worker try/catch. */
    private fun installCrashLogger() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                DownloadDebugLog.e(
                    this,
                    "UncaughtException",
                    "FATAL on thread=${thread.name}",
                    throwable
                )
            } catch (_: Exception) {
                Log.e("StreamDropDL", "FATAL on thread=${thread.name}", throwable)
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}

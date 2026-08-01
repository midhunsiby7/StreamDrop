package com.streamdrop.app.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * DownloadForegroundService (Stub for Stage 1)
 *
 * This service will run downloads in the foreground in Stage 3.
 * Declared in AndroidManifest now so the manifest is valid from the start.
 */
class DownloadForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Stage 3: Start foreground notification + WorkManager integration
        return START_NOT_STICKY
    }
}

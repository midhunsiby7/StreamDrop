package com.streamdrop.app.core.util

import android.content.Context
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object YtDlpUpdater {
    private const val PREF_LAST_UPDATE_TIME = "pref_ytdlp_last_update_time"
    private const val UPDATE_INTERVAL_MS = 7L * 24 * 60 * 60 * 1000 // 7 days

    suspend fun updateIfRequired(context: Context) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("ytdlp_prefs", Context.MODE_PRIVATE)
        val lastUpdateTime = prefs.getLong(PREF_LAST_UPDATE_TIME, 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastUpdateTime > UPDATE_INTERVAL_MS) {
            DownloadDebugLog.i(context, "YtDlpUpdater", "Checking for yt-dlp update (last update > 7 days ago)")
            try {
                val status = YoutubeDL.getInstance().updateYoutubeDL(context, YoutubeDL.UpdateChannel.STABLE)
                DownloadDebugLog.i(context, "YtDlpUpdater", "yt-dlp update status: $status")
                
                // Only update the timestamp if it succeeds without exception
                prefs.edit().putLong(PREF_LAST_UPDATE_TIME, currentTime).apply()
            } catch (e: Exception) {
                DownloadDebugLog.e(context, "YtDlpUpdater", "Failed to update yt-dlp (offline or network error)", e)
                // We do not update the timestamp here, so it will try again next app launch
            }
        } else {
            DownloadDebugLog.i(context, "YtDlpUpdater", "yt-dlp update not required. Last check was recent.")
        }
    }
}

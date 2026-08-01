package com.streamdrop.app.core.data.ytdlp

import android.content.Context
import com.google.gson.Gson
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.streamdrop.app.core.util.DownloadDebugLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YtDlpRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val gson: Gson
) {
    private val cache = mutableMapOf<String, CachedMetadata>()

    private data class CachedMetadata(
        val metadata: YtDlpMetadata,
        val timestamp: Long
    )
    
    companion object {
        private const val TAG = "YtDlpRepository"
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }

    /**
     * Analyzes a URL by getting media metadata via YoutubeDL engine.
     */
    suspend fun analyzeUrl(url: String): Result<YtDlpMetadata> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        DownloadDebugLog.i(appContext, TAG, "Analyze started for url: $url")
        try {
            if (url.isBlank()) {
                return@withContext Result.failure(Exception("URL cannot be empty"))
            }

            // Check cache first
            cache[url]?.let { cached ->
                if (System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
                    DownloadDebugLog.i(appContext, TAG, "Analyze returning from cache for url: $url")
                    return@withContext Result.success(cached.metadata)
                }
            }

            val req = YoutubeDLRequest(url).apply {
                addOption("--dump-json")
                addOption("--no-playlist")
                addOption("--skip-download")
                addOption("--no-warnings")
                addOption("--no-cache-dir")
            }

            val pythonStart = System.currentTimeMillis()
            val response = YoutubeDL.getInstance().execute(req, null, null)
            val pythonEnd = System.currentTimeMillis()
            DownloadDebugLog.i(appContext, TAG, "yt-dlp execution took ${pythonEnd - pythonStart}ms for url: $url")

            val jsonOut = response.out
            if (jsonOut.isNullOrBlank()) {
                throw Exception("Empty response from yt-dlp")
            }

            val parseStart = System.currentTimeMillis()
            val metadata = gson.fromJson(jsonOut, YtDlpMetadata::class.java)
            val parseEnd = System.currentTimeMillis()
            DownloadDebugLog.i(appContext, TAG, "JSON parsing took ${parseEnd - parseStart}ms for url: $url")

            // Save to cache
            cache[url] = CachedMetadata(metadata, System.currentTimeMillis())

            val totalTime = System.currentTimeMillis() - startTime
            DownloadDebugLog.i(appContext, TAG, "Analyze fully completed in ${totalTime}ms for url: $url")
            
            Result.success(metadata)
        } catch (e: com.yausername.youtubedl_android.YoutubeDLException) {
            val errorMsg = "YtDlp Exception: ${e.message?.take(500)}\nCause: ${e.cause?.message?.take(200)}"
            DownloadDebugLog.e(appContext, TAG, "Analyze failed (YoutubeDLException) after ${System.currentTimeMillis() - startTime}ms: $errorMsg", e)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            DownloadDebugLog.e(appContext, TAG, "Analyze failed after ${System.currentTimeMillis() - startTime}ms", e)
            Result.failure(Exception(e.message?.take(500) ?: "Unknown Error"))
        }
    }
}

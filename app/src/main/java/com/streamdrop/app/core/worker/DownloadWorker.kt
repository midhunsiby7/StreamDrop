package com.streamdrop.app.core.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.streamdrop.app.R
import com.streamdrop.app.core.data.db.DownloadDao
import com.streamdrop.app.core.data.db.DownloadStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadDao: DownloadDao
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "download_channel"
    private val notificationId = inputData.getLong(KEY_DOWNLOAD_ID, 0L).toInt()
    
    // yt-dlp outputs progress like "[download]  45.3% of 10.2MiB at 1.2MiB/s"
    private val progressPattern = Pattern.compile("\\[download\\]\\s+([0-9.]+)%")
    private val errorPattern = Pattern.compile("ERROR:.*")

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val downloadId = inputData.getLong(KEY_DOWNLOAD_ID, -1L)
        if (downloadId == -1L) return@withContext Result.failure()

        val download = downloadDao.getDownloadById(downloadId) ?: return@withContext Result.failure()

        createNotificationChannel()
        setForeground(createForegroundInfo(download.title, 0))

        downloadDao.updateStatus(downloadId, DownloadStatus.DOWNLOADING)

        try {
            val executable = getExecutablePath()
            
            // Build the yt-dlp command
            val args = mutableListOf(
                executable,
                "--newline", // Required to parse progress
                "-o", download.destinationPath,
            )
            
            if (download.formatId != null && download.formatId != "best") {
                args.add("-f")
                args.add(download.formatId)
            }
            
            args.add(download.url)
            
            val processBuilder = ProcessBuilder(args)
            val process = processBuilder.start()

            // Read output and update progress
            process.inputStream.bufferedReader().use { reader ->
                var line: String?
                var lastUpdateTime = 0L
                while (reader.readLine().also { line = it } != null) {
                    line?.let { outputLine ->
                        val matcher = progressPattern.matcher(outputLine)
                        if (matcher.find()) {
                            val percentStr = matcher.group(1)
                            percentStr?.toFloatOrNull()?.let { percent ->
                                val progress = percent / 100f
                                
                                // Throttle DB and Notification updates to every 500ms
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastUpdateTime > 500) {
                                    lastUpdateTime = currentTime
                                    downloadDao.updateProgress(
                                        id = downloadId,
                                        progress = progress,
                                        downloadedBytes = (download.totalBytes * progress).toLong(), // Estimate
                                        status = DownloadStatus.DOWNLOADING
                                    )
                                    
                                    notificationManager.notify(
                                        notificationId, 
                                        createNotification(download.title, (progress * 100).toInt())
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val exitCode = process.waitFor()

            if (exitCode == 0) {
                downloadDao.updateProgress(
                    id = downloadId,
                    progress = 1.0f,
                    downloadedBytes = download.totalBytes,
                    status = DownloadStatus.COMPLETED
                )
                
                // Show completion notification
                val completeNotification = NotificationCompat.Builder(appContext, channelId)
                    .setContentTitle("Download Complete")
                    .setContentText(download.title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build()
                notificationManager.notify(notificationId, completeNotification)
                
                Result.success()
            } else {
                val error = process.errorStream.bufferedReader().use { it.readText() }
                val userFriendlyError = when {
                    error.contains("Sign in to confirm your age", ignoreCase = true) -> "Age-restricted content. Please login."
                    error.contains("Video unavailable", ignoreCase = true) -> "Video is unavailable or private."
                    error.contains("Incomplete YouTube URL", ignoreCase = true) -> "Invalid YouTube URL."
                    else -> "Download failed. Please try again."
                }
                
                downloadDao.updateStatus(downloadId, DownloadStatus.FAILED)
                
                // Show error notification
                val errorNotification = NotificationCompat.Builder(appContext, channelId)
                    .setContentTitle("Download Failed")
                    .setContentText(userFriendlyError)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build()
                notificationManager.notify(notificationId, errorNotification)
                
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMessage = e.message ?: "Unknown error occurred"
            downloadDao.updateStatus(downloadId, DownloadStatus.FAILED)
            
            val errorNotification = NotificationCompat.Builder(appContext, channelId)
                .setContentTitle("Download Error")
                .setContentText(errorMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
            notificationManager.notify(notificationId, errorNotification)

            Result.failure()
        }
    }

    private fun getExecutablePath(): String {
        val nativeLibraryDir = appContext.applicationInfo.nativeLibraryDir
        val ytdlpFile = File(nativeLibraryDir, "libytdlp.so")
        
        if (!ytdlpFile.exists()) {
            throw IllegalStateException("yt-dlp binary not found")
        }
        
        ytdlpFile.setExecutable(true, false)
        return ytdlpFile.absolutePath
    }

    private fun createForegroundInfo(title: String, progress: Int): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, createNotification(title, progress), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, createNotification(title, progress))
        }
    }

    private fun createNotification(title: String, progress: Int): Notification {
        return NotificationCompat.Builder(appContext, channelId)
            .setContentTitle("Downloading: $title")
            .setContentText("$progress%")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW // Low importance so it doesn't make sound
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val KEY_DOWNLOAD_ID = "download_id"
    }
}

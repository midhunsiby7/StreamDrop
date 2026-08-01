package com.streamdrop.app.core.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.streamdrop.app.R
import com.streamdrop.app.core.data.db.DownloadDao
import com.streamdrop.app.core.data.db.DownloadStatus
import com.streamdrop.app.core.util.DownloadDebugLog
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadDao: DownloadDao
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "download_channel"
    private val downloadId = inputData.getLong(KEY_DOWNLOAD_ID, -1L)
    private val notificationId = (downloadId % Int.MAX_VALUE).toInt().coerceAtLeast(1)

    override suspend fun getForegroundInfo(): ForegroundInfo {
        DownloadDebugLog.i(appContext, TAG, "getForegroundInfo() enter downloadId=$downloadId")
        return try {
            createNotificationChannel()
            val title = downloadDao.getDownloadById(downloadId)?.title ?: "Downloading"
            val info = createForegroundInfo(title, 0)
            DownloadDebugLog.i(appContext, TAG, "getForegroundInfo() success title=$title notificationId=$notificationId")
            info
        } catch (t: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "getForegroundInfo() FAILED", t)
            throw t
        }
    }

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.IO) {
                runDownloadWork()
            }
        } catch (c: kotlinx.coroutines.CancellationException) {
            DownloadDebugLog.i(appContext, TAG, "doWork() CANCELLED for downloadId=$downloadId")
            try {
                if (downloadId != -1L) {
                    YoutubeDL.getInstance().destroyProcessById(downloadId.toString())
                    downloadDao.updateStatus(downloadId, DownloadStatus.CANCELED)
                }
            } catch (_: Throwable) {}
            Result.failure()
        } catch (t: Throwable) {
            // Absolute outer safety net — nothing should escape unlogged.
            DownloadDebugLog.e(appContext, TAG, "doWork() UNCAUGHT throwable downloadId=$downloadId", t)
            try {
                if (downloadId != -1L) {
                    downloadDao.updateStatus(downloadId, DownloadStatus.FAILED)
                }
            } catch (dbError: Throwable) {
                DownloadDebugLog.e(appContext, TAG, "Failed to mark download FAILED after crash", dbError)
            }
            Result.failure()
        }
    }

    private suspend fun runDownloadWork(): Result {
        DownloadDebugLog.i(
            appContext,
            TAG,
            "STEP 1 Worker starts id=$downloadId runAttempt=$runAttemptCount " +
                "api=${Build.VERSION.SDK_INT} package=${appContext.packageName}"
        )

        if (downloadId == -1L) {
            DownloadDebugLog.e(appContext, TAG, "Invalid downloadId=-1 in inputData")
            return Result.failure()
        }

        val download = try {
            downloadDao.getDownloadById(downloadId)
        } catch (t: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "STEP 1b DB getDownloadById failed", t)
            throw t
        }

        if (download == null) {
            DownloadDebugLog.e(appContext, TAG, "No DownloadEntity for id=$downloadId")
            return Result.failure()
        }

        DownloadDebugLog.i(
            appContext,
            TAG,
            "STEP 1c Loaded entity url=${download.url} dest=${download.destinationPath} " +
                "formatId=${download.formatId} title=${download.title}"
        )

        try {
            createNotificationChannel()
            DownloadDebugLog.i(appContext, TAG, "STEP 2 Notification channel ready")
        } catch (t: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "STEP 2 createNotificationChannel FAILED", t)
            throw t
        }

        try {
            val fg = getForegroundInfo()
            DownloadDebugLog.i(appContext, TAG, "STEP 3 Calling setForeground()")
            setForeground(fg)
            DownloadDebugLog.i(appContext, TAG, "STEP 3 Foreground notification set OK")
        } catch (t: Throwable) {
            DownloadDebugLog.e(
                appContext,
                TAG,
                "STEP 3 setForeground/getForegroundInfo FAILED — continuing without foreground",
                t
            )
        }

        try {
            downloadDao.updateStatus(downloadId, DownloadStatus.DOWNLOADING)
            DownloadDebugLog.i(appContext, TAG, "STEP 4 Status -> DOWNLOADING")
        } catch (t: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "STEP 4 updateStatus FAILED", t)
            throw t
        }

        val outputPath = try {
            val path = ensureOutputTemplate(download.destinationPath)
            val parent = File(path).parentFile
            DownloadDebugLog.i(
                appContext,
                TAG,
                "STEP 5 Output template=$path parentExists=${parent?.exists()} " +
                    "parentCanWrite=${parent?.canWrite()} parent=${parent?.absolutePath}"
            )
            path
        } catch (t: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "STEP 5 Output directory resolve FAILED", t)
            throw t
        }

        val request = try {
            val req = YoutubeDLRequest(download.url)
            req.addOption("-o", outputPath)
            req.addOption("--no-mtime")
            req.addOption("--newline")
            req.addOption("--extractor-args", "youtube:player_client=android,web")
            req.addOption("--buffer-size", "16K")
            req.addOption("--no-cache-dir")
            if (com.streamdrop.app.BuildConfig.DEBUG) {
                req.addOption("--verbose")
            }
            if (download.formatId == "audio:mp3") {
                // Use bestaudio/best fallback chain:
                // YouTube's SABR experiment strips URLs from audio-only streams.
                // If bestaudio is unavailable, /best downloads a combined stream
                // and --extract-audio extracts the audio track via FFmpeg.
                req.addOption("-f", "bestaudio/best")
                req.addOption("--extract-audio")
                req.addOption("--audio-format", "mp3")
                req.addOption("--audio-quality", "192K")
            } else if (download.formatId != null && download.formatId != "best") {
                req.addOption("--concurrent-fragments", 4)
                req.addOption("-f", download.formatId)
            } else {
                req.addOption("--concurrent-fragments", 4)
                // Modified format selection to be more robust for YouTube
                req.addOption(
                    "-f",
                    "bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4]/best"
                )
            }
            // Fix for 403 Forbidden on some videos
            req.addOption("--no-check-certificate")
            req.addOption("--user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")

            DownloadDebugLog.i(
                appContext,
                TAG,
                "STEP 6 YoutubeDLRequest built command=${req.buildCommand()}"
            )
            req
        } catch (t: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "STEP 6 YoutubeDLRequest build FAILED", t)
            throw t
        }

        try {
            DownloadDebugLog.i(appContext, TAG, "STEP 7 Calling YoutubeDL.execute() processId=$downloadId")
            var lastUpdateTime = 0L
            var progressLogCount = 0
            var activeTotalBytes = download.totalBytes

            val response = YoutubeDL.getInstance().execute(request, downloadId.toString()) { progress, eta, line ->
                try {
                    val parsedSize = parseTotalBytesFromYtDlpLine(line)
                    if (parsedSize != null && parsedSize > 0) {
                        activeTotalBytes = parsedSize
                    }

                    // Log the first few lines and then every 25% to avoid log spam
                    if (progressLogCount < 5 || progress.toInt() % 25 == 0) {
                        DownloadDebugLog.i(
                            appContext,
                            TAG,
                            "STEP 8 Progress callback progress=$progress eta=$eta line=${line.take(120)}"
                        )
                        progressLogCount++
                    }

                    if (isStopped) {
                        DownloadDebugLog.i(appContext, TAG, "STEP 8 Worker stopped — destroying process")
                        YoutubeDL.getInstance().destroyProcessById(downloadId.toString())
                        return@execute
                    }

                    // progress < 0 means it's still extracting or preparing
                    val currentProgress = if (progress < 0) 0f else (progress / 100f).coerceIn(0f, 1f)
                    val currentTime = System.currentTimeMillis()
                    
                    if (currentTime - lastUpdateTime > 400) {
                        lastUpdateTime = currentTime
                        val currentDownloadedBytes = if (activeTotalBytes > 0) {
                            (activeTotalBytes * currentProgress).toLong()
                        } else 0L

                        runBlocking {
                            downloadDao.updateProgress(
                                id = downloadId,
                                progress = currentProgress,
                                downloadedBytes = currentDownloadedBytes,
                                totalBytes = activeTotalBytes,
                                status = DownloadStatus.DOWNLOADING
                            )
                        }
                        
                        val statusText = if (progress < 0) "Extracting..." else "${progress.toInt()}%"
                        notificationManager.notify(
                            notificationId,
                            createNotification(download.title, (currentProgress * 100).toInt(), statusText)
                        )
                    }
                } catch (t: Throwable) {
                    DownloadDebugLog.e(appContext, TAG, "STEP 8 Progress callback exception", t)
                }
            }

            DownloadDebugLog.i(appContext, TAG, "STEP 9 YoutubeDL.execute() returned normally")
        } catch (e: com.yausername.youtubedl_android.YoutubeDLException) {
            val fullMessage = e.message ?: "YoutubeDL exception"
            DownloadDebugLog.e(appContext, TAG, "YoutubeDL.execute() failed: $fullMessage", e)
            handleFailure("YtDlp: ${fullMessage.take(400)}")
            return Result.failure()
        } catch (t: Throwable) {
            val fullMessage = t.message ?: "Unknown error"
            DownloadDebugLog.e(appContext, TAG, "Download error: $fullMessage", t)
            handleFailure("Error: ${fullMessage.take(400)}")
            return Result.failure()
        }

        if (isStopped) {
            DownloadDebugLog.i(appContext, TAG, "STEP 10 Stopped after execute — CANCELED")
            downloadDao.updateStatus(downloadId, DownloadStatus.CANCELED)
            return Result.failure()
        }

        try {
            val finalFile = resolveOutputFile(outputPath, download.destinationPath)
            val exists = finalFile?.exists() == true
            val size = finalFile?.length() ?: 0L
            
            DownloadDebugLog.i(
                appContext,
                TAG,
                "STEP 10 Resolve output final=${finalFile?.absolutePath} exists=$exists size=$size"
            )

            if (!exists || size == 0L) {
                val errorMsg = if (!exists) "Output file not found" else "Output file is empty"
                DownloadDebugLog.e(appContext, TAG, "STEP 11 Validation FAILED: $errorMsg")
                downloadDao.updateFailure(
                    id = downloadId,
                    status = DownloadStatus.FAILED,
                    errorMessage = errorMsg
                )
                return Result.failure()
            }

            // Successfully verified the file
            val finalPath = finalFile!!.absolutePath
            
            // Task 5: MediaStore index scanning
            MediaScannerConnection.scanFile(
                appContext,
                arrayOf(finalPath),
                null
            ) { path, uri ->
                DownloadDebugLog.i(appContext, TAG, "MediaScanner scanned path=$path uri=$uri")
            }
            
            // Final 100% progress notification
            notificationManager.notify(
                notificationId,
                createNotification(download.title, 100, "100%")
            )

            downloadDao.updateCompleted(
                id = downloadId,
                destinationPath = finalPath,
                progress = 1.0f,
                downloadedBytes = size,
                totalBytes = size
            )

            notificationManager.notify(
                notificationId,
                NotificationCompat.Builder(appContext, channelId)
                    .setContentTitle("Download Complete")
                    .setContentText(download.title)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .build()
            )

            DownloadDebugLog.i(appContext, TAG, "STEP 11 Download COMPLETED id=$downloadId path=$finalPath")
            return Result.success()
        } catch (t: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "STEP 10/11 Completion bookkeeping FAILED", t)
            downloadDao.updateFailure(downloadId, DownloadStatus.FAILED, t.message ?: "Completion error")
            return Result.failure()
        }
    }

    private fun createForegroundInfo(title: String, progress: Int): ForegroundInfo {
        val notification = createNotification(title, progress, "Starting...")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationId,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private fun createNotification(title: String, progress: Int, statusText: String): Notification {
        return NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(statusText)
            .setContentText(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setProgress(100, progress.coerceIn(0, 100), progress <= 0)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Download progress"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun ensureOutputTemplate(destinationPath: String): String {
        val file = File(destinationPath)
        file.parentFile?.mkdirs()
        val name = file.name
        return if (name.contains("%(")) {
            destinationPath
        } else {
            val base = name.substringBeforeLast('.') // e.g. 'Malang' from 'Malang.mp3'
            File(file.parentFile, "$base.%(ext)s").absolutePath
        }
    }

    private fun resolveOutputFile(templatePath: String, originalPath: String): File? {
        val templateFile = File(templatePath)
        val parent = templateFile.parentFile ?: return File(originalPath).takeIf { it.exists() }
        
        // The template ends with .%(ext)s
        val prefix = templateFile.name.substringBefore(".%(ext)s")
        
        // Because parent.listFiles() often returns null in Scoped Storage (Android 10+)
        // we must manually check for the most common yt-dlp output extensions.
        val extensions = listOf(".mp3", ".mp4", ".m4a", ".webm", ".mkv")
        for (ext in extensions) {
            val f = File(parent, prefix + ext)
            if (f.exists() && f.length() > 0) {
                DownloadDebugLog.i(appContext, TAG, "resolveOutputFile: found matching extension file ${f.name}")
                return f
            }
        }
        
        // Fallback: Find any file in the parent directory that starts with this prefix and isn't a temp file
        val match = parent.listFiles()
            ?.filter { 
                it.isFile && 
                it.name.startsWith(prefix) && 
                !it.name.endsWith(".part") && 
                !it.name.endsWith(".ytdl") 
            }
            ?.maxByOrNull { it.lastModified() }
            
        DownloadDebugLog.i(
            appContext,
            TAG,
            "resolveOutputFile: fallback listFiles match=${match?.name}"
        )

        return match ?: File(originalPath).takeIf { it.exists() }
    }

    private fun handleFailure(errorMsg: String) {
        try {
            if (isStopped) {
                runBlocking { downloadDao.updateStatus(downloadId, DownloadStatus.CANCELED) }
            } else {
                runBlocking {
                    downloadDao.updateFailure(
                        id = downloadId,
                        status = DownloadStatus.FAILED,
                        errorMessage = errorMsg
                    )
                }
                notificationManager.notify(
                    notificationId,
                    NotificationCompat.Builder(appContext, channelId)
                        .setContentTitle("Download Failed")
                        .setContentText(errorMsg.take(100))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true)
                        .build()
                )
            }
        } catch (statusError: Throwable) {
            DownloadDebugLog.e(appContext, TAG, "Failed updating status after execute error", statusError)
        }
    }

    private fun parseTotalBytesFromYtDlpLine(line: String?): Long? {
        if (line.isNullOrBlank()) return null
        val match = Regex("""of\s+~?\s*([\d.]+)\s*([KMGTkmg]?i?B)""", RegexOption.IGNORE_CASE).find(line)
            ?: return null
        val value = match.groupValues[1].toDoubleOrNull() ?: return null
        val unit = match.groupValues[2].uppercase(java.util.Locale.US)

        val multiplier = when {
            unit.startsWith("K") -> 1024L
            unit.startsWith("M") -> 1024L * 1024L
            unit.startsWith("G") -> 1024L * 1024L * 1024L
            unit.startsWith("T") -> 1024L * 1024L * 1024L * 1024L
            else -> 1L
        }
        return (value * multiplier).toLong()
    }

    companion object {
        const val KEY_DOWNLOAD_ID = "download_id"
        private const val TAG = "DownloadWorker"
    }
}

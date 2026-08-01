package com.streamdrop.app.core.data.repository

import android.content.Context
import android.os.Environment
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.streamdrop.app.core.data.db.DownloadDao
import com.streamdrop.app.core.data.db.DownloadEntity
import com.streamdrop.app.core.data.db.DownloadStatus
import com.streamdrop.app.core.util.DownloadDebugLog
import com.streamdrop.app.core.worker.DownloadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadDao: DownloadDao
) {

    fun getAllDownloads(): Flow<List<DownloadEntity>> = downloadDao.getAllDownloads()

    fun getActiveDownloads(): Flow<List<DownloadEntity>> = downloadDao.getActiveDownloads()

    suspend fun startDownload(
        url: String,
        title: String,
        thumbnail: String?,
        formatId: String?,
        estimatedSize: Long,
        fileName: String
    ): Long {
        DownloadDebugLog.i(context, TAG, "Repository.startDownload begin")
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val streamDropDir = File(downloadsDir, "StreamDrop")
            val created = if (!streamDropDir.exists()) streamDropDir.mkdirs() else true
            DownloadDebugLog.i(
                context,
                TAG,
                "Output dir=${streamDropDir.absolutePath} exists=${streamDropDir.exists()} " +
                    "created=$created canWrite=${streamDropDir.canWrite()}"
            )

            val destinationFile = getUniqueFile(streamDropDir, fileName)
            val destinationPath = destinationFile.absolutePath
            DownloadDebugLog.i(context, TAG, "destinationPath=$destinationPath")

            val entity = DownloadEntity(
                url = url,
                title = title,
                thumbnail = thumbnail,
                formatId = formatId,
                destinationPath = destinationPath,
                status = DownloadStatus.PENDING,
                progress = 0f,
                totalBytes = estimatedSize,
                downloadedBytes = 0L
            )

            val downloadId = downloadDao.insertDownload(entity)
            DownloadDebugLog.i(context, TAG, "Inserted DownloadEntity id=$downloadId status=PENDING")

            val workData = workDataOf(DownloadWorker.KEY_DOWNLOAD_ID to downloadId)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .setInputData(workData)
                .addTag("download_$downloadId")
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            DownloadDebugLog.i(
                context,
                TAG,
                "Enqueue WorkManager id=${downloadRequest.id} tag=download_$downloadId expedited=true"
            )
            WorkManager.getInstance(context).enqueue(downloadRequest)
            DownloadDebugLog.i(context, TAG, "WorkManager.enqueue returned")

            return downloadId
        } catch (t: Throwable) {
            DownloadDebugLog.e(context, TAG, "Repository.startDownload FAILED", t)
            throw t
        }
    }

    suspend fun cancelDownload(downloadId: Long) {
        DownloadDebugLog.i(context, TAG, "cancelDownload called for id=$downloadId")
        try {
            com.yausername.youtubedl_android.YoutubeDL.getInstance().destroyProcessById(downloadId.toString())
        } catch (e: Exception) {
            DownloadDebugLog.e(context, TAG, "Error destroying process for id=$downloadId", e)
        }
        downloadDao.updateStatus(downloadId, DownloadStatus.CANCELED)
        WorkManager.getInstance(context).cancelAllWorkByTag("download_$downloadId")
    }

    suspend fun pauseDownload(downloadId: Long) {
        try {
            com.yausername.youtubedl_android.YoutubeDL.getInstance().destroyProcessById(downloadId.toString())
        } catch (_: Exception) {}
        downloadDao.updateStatus(downloadId, DownloadStatus.PAUSED)
        WorkManager.getInstance(context).cancelAllWorkByTag("download_$downloadId")
    }

    suspend fun resumeDownload(download: DownloadEntity) {
        val workData = workDataOf(DownloadWorker.KEY_DOWNLOAD_ID to download.id)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(workData)
            .addTag("download_${download.id}")
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueue(downloadRequest)
        downloadDao.updateStatus(download.id, DownloadStatus.DOWNLOADING)
    }

    suspend fun deleteDownload(downloadId: Long) {
        downloadDao.deleteDownload(downloadId)
    }

    suspend fun clearAllDownloads() {
        downloadDao.clearAllDownloads()

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val streamDropDir = File(downloadsDir, "StreamDrop")
        if (streamDropDir.exists()) {
            streamDropDir.deleteRecursively()
            streamDropDir.mkdirs()
        }
    }

    private fun getUniqueFile(dir: File, fileName: String): File {
        val cleanName = fileName.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim().ifEmpty { "download" }
        var targetFile = File(dir, cleanName)
        if (!targetFile.exists()) return targetFile

        val hasExt = cleanName.contains('.') && !cleanName.startsWith('.')
        val nameWithoutExt = if (hasExt) cleanName.substringBeforeLast('.') else cleanName
        val ext = if (hasExt) "." + cleanName.substringAfterLast('.') else ""

        var counter = 1
        while (targetFile.exists()) {
            targetFile = File(dir, "$nameWithoutExt ($counter)$ext")
            counter++
        }
        return targetFile
    }

    companion object {
        private const val TAG = "DownloadRepository"
    }
}

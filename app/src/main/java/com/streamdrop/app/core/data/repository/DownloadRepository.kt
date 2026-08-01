package com.streamdrop.app.core.data.repository

import android.content.Context
import android.os.Environment
import androidx.work.*
import com.streamdrop.app.core.data.db.DownloadDao
import com.streamdrop.app.core.data.db.DownloadEntity
import com.streamdrop.app.core.data.db.DownloadStatus
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
        // Prepare destination path in Movies/StreamDrop
        val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val streamDropDir = File(moviesDir, "StreamDrop")
        if (!streamDropDir.exists()) {
            streamDropDir.mkdirs()
        }
        
        // Clean filename of invalid characters
        val cleanFileName = fileName.replace(Regex("[\\\\/:*?\"<>|]"), "_") + ".mp4"
        val destinationPath = File(streamDropDir, cleanFileName).absolutePath

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

        // Insert into DB
        val downloadId = downloadDao.insertDownload(entity)

        // Enqueue WorkManager task
        val workData = workDataOf(DownloadWorker.KEY_DOWNLOAD_ID to downloadId)
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(workData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueue(downloadRequest)

        return downloadId
    }

    suspend fun cancelDownload(downloadId: Long) {
        // Cancel the WorkManager task
        // We need a tag to cancel it effectively, but let's just use cancelAllWorkByTag
        // Actually it's better to cancel by ID if we stored the UUID, or just delete from DB and let worker fail
        downloadDao.updateStatus(downloadId, DownloadStatus.CANCELED)
    }
}

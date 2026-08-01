package com.streamdrop.app.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: Long): DownloadEntity?

    @Query("SELECT * FROM downloads WHERE status != 'CANCELED' ORDER BY timestamp DESC LIMIT 50")
    fun getActiveDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity): Long

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: DownloadStatus)

    @Query(
        "UPDATE downloads SET progress = :progress, downloadedBytes = :downloadedBytes, " +
            "totalBytes = CASE WHEN :totalBytes > 0 THEN :totalBytes ELSE totalBytes END, " +
            "status = :status, errorMessage = NULL WHERE id = :id"
    )
    suspend fun updateProgress(
        id: Long,
        progress: Float,
        downloadedBytes: Long,
        totalBytes: Long = -1L,
        status: DownloadStatus
    )

    @Query(
        "UPDATE downloads SET status = :status, errorMessage = :errorMessage, progress = :progress WHERE id = :id"
    )
    suspend fun updateFailure(
        id: Long,
        status: DownloadStatus,
        errorMessage: String,
        progress: Float = 0f
    )

    @Query(
        "UPDATE downloads SET status = :status, destinationPath = :destinationPath, " +
            "progress = :progress, downloadedBytes = :downloadedBytes, totalBytes = :totalBytes, " +
            "errorMessage = NULL WHERE id = :id"
    )
    suspend fun updateCompleted(
        id: Long,
        destinationPath: String,
        progress: Float,
        downloadedBytes: Long,
        totalBytes: Long,
        status: DownloadStatus = DownloadStatus.COMPLETED
    )

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: Long)

    @Query("DELETE FROM downloads")
    suspend fun clearAllDownloads()
}

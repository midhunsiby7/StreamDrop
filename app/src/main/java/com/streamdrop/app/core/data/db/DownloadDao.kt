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
    
    @Query("SELECT * FROM downloads WHERE status = 'DOWNLOADING' OR status = 'PENDING'")
    fun getActiveDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity): Long

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: DownloadStatus)

    @Query("UPDATE downloads SET progress = :progress, downloadedBytes = :downloadedBytes, status = :status WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Float, downloadedBytes: Long, status: DownloadStatus)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: Long)
}

package com.streamdrop.app.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val thumbnail: String?,
    val formatId: String?,
    val destinationPath: String,
    val status: DownloadStatus,
    val progress: Float, // 0.0 to 1.0
    val totalBytes: Long,
    val downloadedBytes: Long,
    val timestamp: Long = System.currentTimeMillis(),
    /** Human-readable failure reason (null when not failed). */
    val errorMessage: String? = null
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELED
}

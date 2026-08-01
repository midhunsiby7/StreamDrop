package com.streamdrop.app.feature.download

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamdrop.app.core.data.db.DownloadEntity
import com.streamdrop.app.core.data.repository.DownloadRepository
import com.streamdrop.app.core.util.DownloadDebugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val application: Application,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val downloadExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        DownloadDebugLog.e(application, TAG, "Uncaught coroutine exception in DownloadViewModel", throwable)
    }

    val activeDownloads: StateFlow<List<DownloadEntity>> = downloadRepository.getActiveDownloads()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun startDownload(
        url: String,
        title: String,
        thumbnail: String?,
        formatId: String?,
        estimatedSize: Long,
        fileName: String
    ) {
        DownloadDebugLog.i(
            application,
            TAG,
            "startDownload() called url=$url title=$title formatId=$formatId fileName=$fileName size=$estimatedSize"
        )
        viewModelScope.launch(downloadExceptionHandler) {
            try {
                val id = downloadRepository.startDownload(
                    url, title, thumbnail, formatId, estimatedSize, fileName
                )
                DownloadDebugLog.i(application, TAG, "startDownload() enqueued id=$id")
            } catch (t: Throwable) {
                DownloadDebugLog.e(application, TAG, "startDownload() FAILED", t)
            }
        }
    }

    fun cancelDownload(downloadId: Long) {
        viewModelScope.launch(downloadExceptionHandler) {
            try {
                downloadRepository.cancelDownload(downloadId)
            } catch (t: Throwable) {
                DownloadDebugLog.e(application, TAG, "cancelDownload($downloadId) FAILED", t)
            }
        }
    }

    fun pauseDownload(downloadId: Long) {
        viewModelScope.launch(downloadExceptionHandler) {
            try {
                downloadRepository.pauseDownload(downloadId)
            } catch (t: Throwable) {
                DownloadDebugLog.e(application, TAG, "pauseDownload($downloadId) FAILED", t)
            }
        }
    }

    fun resumeDownload(download: DownloadEntity) {
        viewModelScope.launch(downloadExceptionHandler) {
            try {
                downloadRepository.resumeDownload(download)
            } catch (t: Throwable) {
                DownloadDebugLog.e(application, TAG, "resumeDownload(${download.id}) FAILED", t)
            }
        }
    }

    fun retryDownload(download: DownloadEntity) {
        viewModelScope.launch(downloadExceptionHandler) {
            try {
                // Restarting failed download via repository
                downloadRepository.resumeDownload(download)
            } catch (t: Throwable) {
                DownloadDebugLog.e(application, TAG, "retryDownload(${download.id}) FAILED", t)
            }
        }
    }

    fun deleteDownload(downloadId: Long) {
        viewModelScope.launch(downloadExceptionHandler) {
            try {
                downloadRepository.deleteDownload(downloadId)
            } catch (t: Throwable) {
                DownloadDebugLog.e(application, TAG, "deleteDownload($downloadId) FAILED", t)
            }
        }
    }

    fun playDownload(download: DownloadEntity) {
        try {
            val file = java.io.File(download.destinationPath)
            if (!file.exists()) return
            val uri = androidx.core.content.FileProvider.getUriForFile(
                application,
                "${application.packageName}.provider",
                file
            )
            val extension = file.extension.lowercase()
            val mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            application.startActivity(intent)
        } catch (t: Throwable) {
            DownloadDebugLog.e(application, TAG, "playDownload FAILED", t)
        }
    }

    fun shareDownload(download: DownloadEntity) {
        try {
            val file = java.io.File(download.destinationPath)
            if (!file.exists()) return
            val uri = androidx.core.content.FileProvider.getUriForFile(
                application,
                "${application.packageName}.provider",
                file
            )
            val extension = file.extension.lowercase()
            val mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = android.content.Intent.createChooser(intent, "Share").apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            application.startActivity(chooser)
        } catch (t: Throwable) {
            DownloadDebugLog.e(application, TAG, "shareDownload FAILED", t)
        }
    }

    companion object {
        private const val TAG = "DownloadViewModel"
    }
}

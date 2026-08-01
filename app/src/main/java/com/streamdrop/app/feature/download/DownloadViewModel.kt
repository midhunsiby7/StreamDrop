package com.streamdrop.app.feature.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamdrop.app.core.data.db.DownloadEntity
import com.streamdrop.app.core.data.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    // Expose all active downloads (PENDING or DOWNLOADING)
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
        viewModelScope.launch {
            downloadRepository.startDownload(url, title, thumbnail, formatId, estimatedSize, fileName)
        }
    }

    fun cancelDownload(downloadId: Long) {
        viewModelScope.launch {
            downloadRepository.cancelDownload(downloadId)
        }
    }
}

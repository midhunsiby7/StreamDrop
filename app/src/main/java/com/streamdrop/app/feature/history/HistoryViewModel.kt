package com.streamdrop.app.feature.history

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
import java.io.File

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    val allDownloads: StateFlow<List<DownloadEntity>> = downloadRepository.getAllDownloads()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteDownload(download: DownloadEntity) {
        viewModelScope.launch {
            // Delete file from storage
            val file = File(download.destinationPath)
            if (file.exists()) {
                file.delete()
            }
            
            // Delete from DB
            downloadRepository.deleteDownload(download.id)
        }
    }
}

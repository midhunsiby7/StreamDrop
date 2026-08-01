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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allDownloads: StateFlow<List<DownloadEntity>> = downloadRepository.getAllDownloads()
        .combine(_searchQuery) { downloads, query ->
            if (query.isEmpty()) {
                downloads
            } else {
                downloads.filter { it.title.contains(query, ignoreCase = true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

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

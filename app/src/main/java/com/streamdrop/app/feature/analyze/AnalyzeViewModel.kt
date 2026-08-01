package com.streamdrop.app.feature.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamdrop.app.core.data.ytdlp.YtDlpMetadata
import com.streamdrop.app.core.data.ytdlp.YtDlpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AnalyzeUiState {
    data object Loading : AnalyzeUiState()
    data class Success(val metadata: YtDlpMetadata) : AnalyzeUiState()
    data class Error(val message: String) : AnalyzeUiState()
}

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val ytDlpRepository: YtDlpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalyzeUiState>(AnalyzeUiState.Loading)
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    fun analyze(url: String) {
        _uiState.value = AnalyzeUiState.Loading
        viewModelScope.launch {
            val result = ytDlpRepository.analyzeUrl(url)
            result.onSuccess { metadata ->
                _uiState.value = AnalyzeUiState.Success(metadata)
            }.onFailure { error ->
                _uiState.value = AnalyzeUiState.Error(error.message ?: "An unknown error occurred")
            }
        }
    }
}

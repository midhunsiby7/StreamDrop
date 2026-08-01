package com.streamdrop.app.feature.settings

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val dataStore = application.dataStore

    private val THEME_KEY = booleanPreferencesKey("dark_theme")
    private val QUALITY_KEY = stringPreferencesKey("default_quality")
    private val CONCURRENT_KEY = intPreferencesKey("max_concurrent_downloads")

    val isDarkTheme: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: true // Default to dark theme
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val defaultQuality: StateFlow<String> = dataStore.data
        .map { preferences ->
            preferences[QUALITY_KEY] ?: "1080p"
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "1080p")

    val maxConcurrentDownloads: StateFlow<Int> = dataStore.data
        .map { preferences ->
            preferences[CONCURRENT_KEY] ?: 3
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = isDark
            }
        }
    }

    fun setDefaultQuality(quality: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[QUALITY_KEY] = quality
            }
        }
    }

    fun setMaxConcurrentDownloads(count: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[CONCURRENT_KEY] = count
            }
        }
    }
}

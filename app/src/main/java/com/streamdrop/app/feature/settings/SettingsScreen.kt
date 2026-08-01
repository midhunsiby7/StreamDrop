package com.streamdrop.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.streamdrop.app.core.ui.components.GlassCard
import com.streamdrop.app.core.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val defaultQuality by viewModel.defaultQuality.collectAsState()
    val maxConcurrent by viewModel.maxConcurrentDownloads.collectAsState()

    val qualities = listOf("Best", "4K", "1080p", "720p", "480p", "Audio Only")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Appearance",
            style = MaterialTheme.typography.labelLarge.copy(color = Violet400)
        )
        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Theme Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                        Text("Use a dark theme for the app", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.setDarkTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Violet500,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = SurfaceElevated
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Downloads",
            style = MaterialTheme.typography.labelLarge.copy(color = Violet400)
        )
        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Default Quality
                var showQualityDialog by remember { mutableStateOf(false) }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showQualityDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Default Quality", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                        Text("Preferred video resolution", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    Text(
                        text = defaultQuality,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Violet400, fontWeight = FontWeight.Bold)
                    )
                }

                if (showQualityDialog) {
                    AlertDialog(
                        onDismissRequest = { showQualityDialog = false },
                        title = { Text("Select Quality") },
                        text = {
                            Column {
                                qualities.forEach { quality ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.setDefaultQuality(quality)
                                                showQualityDialog = false
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = (quality == defaultQuality),
                                            onClick = null
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(quality)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showQualityDialog = false }) {
                                Text("Cancel")
                            }
                        },
                        containerColor = SurfaceElevated,
                        titleContentColor = TextPrimary,
                        textContentColor = TextPrimary
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BorderSubtle)
                
                // Concurrent Downloads
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Concurrent Downloads", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                            Text("Maximum active tasks: $maxConcurrent", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }
                    Slider(
                        value = maxConcurrent.toFloat(),
                        onValueChange = { viewModel.setMaxConcurrentDownloads(it.toInt()) },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = Violet400,
                            activeTrackColor = Violet400,
                            inactiveTrackColor = BorderSubtle
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // About / Info
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Rounded.Info,
                    contentDescription = null,
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("StreamDrop v1.0.0", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary))
                    Text("Built with yt-dlp and Jetpack Compose", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                }
            }
        }
    }
}

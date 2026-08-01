package com.streamdrop.app.feature.download

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.streamdrop.app.core.data.db.DownloadEntity
import com.streamdrop.app.core.data.db.DownloadStatus
import com.streamdrop.app.core.ui.components.*
import com.streamdrop.app.core.ui.theme.*

@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val activeDownloads by viewModel.activeDownloads.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text  = "Downloads",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (activeDownloads.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No active downloads.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(activeDownloads, key = { it.id }) { download ->
                    DownloadItem(
                        download = download,
                        onCancel = { viewModel.cancelDownload(download.id) },
                        onPause = { viewModel.pauseDownload(download.id) },
                        onResume = { viewModel.resumeDownload(download) },
                        onRetry = { viewModel.retryDownload(download) },
                        onDelete = { viewModel.deleteDownload(download.id) },
                        onPlay = { viewModel.playDownload(download) },
                        onShare = { viewModel.shareDownload(download) }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadItem(
    download: DownloadEntity,
    onCancel: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    onShare: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = download.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = download.title,
                    style = MaterialTheme.typography.labelLarge.copy(color = TextPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                
                Text(
                    text = when (download.status) {
                        DownloadStatus.COMPLETED -> "Completed" + (if (download.totalBytes > 0) " • ${formatBytes(download.totalBytes)}" else "")
                        DownloadStatus.FAILED -> "Failed"
                        DownloadStatus.PAUSED -> "Paused" + (if (download.downloadedBytes > 0) " • ${formatBytes(download.downloadedBytes)}" else "")
                        else -> {
                            val pct = (download.progress * 100).toInt()
                            val downloadedStr = if (download.downloadedBytes > 0) formatBytes(download.downloadedBytes) else null
                            val totalStr = if (download.totalBytes > 0) formatBytes(download.totalBytes) else null
                            
                            when {
                                totalStr != null -> "$pct% • $totalStr"
                                downloadedStr != null -> "$pct% • $downloadedStr"
                                else -> if (pct > 0) "$pct%" else "Calculating..."
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = when (download.status) {
                            DownloadStatus.PAUSED -> StatusError
                            DownloadStatus.FAILED -> StatusError
                            DownloadStatus.COMPLETED -> Teal400
                            else -> Violet400
                        }
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (download.status == DownloadStatus.DOWNLOADING || download.status == DownloadStatus.PENDING || download.status == DownloadStatus.PAUSED) {
                    AnimatedProgressBar(
                        progress = download.progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (download.status == DownloadStatus.COMPLETED) {
                    AnimatedProgressBar(
                        progress = 1f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Actions
            when (download.status) {
                DownloadStatus.COMPLETED -> {
                    IconButton(onClick = onPlay) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = "Play", tint = Teal400)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
                DownloadStatus.FAILED -> {
                    IconButton(onClick = onRetry) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = "Retry", tint = Violet400)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
                DownloadStatus.PAUSED -> {
                    IconButton(onClick = onResume) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = "Resume", tint = Violet400)
                    }
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = TextSecondary)
                    }
                }
                else -> {
                    IconButton(onClick = onPause) {
                        Icon(Icons.Rounded.Pause, contentDescription = "Pause", tint = TextSecondary)
                    }
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = TextSecondary)
                    }
                }
            }
        }
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes < 0) return "Unknown"
    if (bytes == 0L) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(java.util.Locale.US, "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

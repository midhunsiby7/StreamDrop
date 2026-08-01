package com.streamdrop.app.feature.download

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
            style = MaterialTheme.typography.displaySmall,
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
                        onCancel = { viewModel.cancelDownload(download.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadItem(
    download: DownloadEntity,
    onCancel: () -> Unit
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
                    text = "${(download.progress * 100).toInt()}% • ${formatBytes(download.downloadedBytes)} / ${formatBytes(download.totalBytes)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Violet400)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                AnimatedProgressBar(
                    progress = download.progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Cancel button
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cancel",
                    tint = TextSecondary
                )
            }
        }
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

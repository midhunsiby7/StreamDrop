package com.streamdrop.app.feature.analyze

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.streamdrop.app.core.data.ytdlp.YtDlpMetadata
import com.streamdrop.app.core.ui.components.*
import com.streamdrop.app.core.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AnalyzeScreen(
    url: String,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    viewModel: AnalyzeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(url) {
        viewModel.analyze(url)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint               = TextPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text  = "Analyze",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "analyze_content"
            ) { state ->
                when (state) {
                    is AnalyzeUiState.Loading -> {
                        AnalyzeLoadingState()
                    }
                    is AnalyzeUiState.Success -> {
                        AnalyzeSuccessState(metadata = state.metadata, url = url, onDownload = onDownload)
                    }
                    is AnalyzeUiState.Error -> {
                        AnalyzeErrorState(message = state.message, onRetry = { viewModel.analyze(url) })
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AnalyzeLoadingState() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Skeleton for Metadata Card
        GlassCard(
            modifier     = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(28.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(20.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.width(60.dp).height(24.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                    Box(modifier = Modifier.width(80.dp).height(24.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                    Box(modifier = Modifier.width(100.dp).height(24.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                }
            }
        }
        
        // Skeleton for Configuration Card
        GlassCard(
            modifier     = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(modifier = Modifier.width(150.dp).height(24.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(25.dp)).shimmerEffect())
            }
        }
    }
}

@Composable
fun AnalyzeSuccessState(
    metadata: YtDlpMetadata,
    url: String,
    onDownload: () -> Unit,
    downloadViewModel: com.streamdrop.app.feature.download.DownloadViewModel = hiltViewModel()
) {
    var downloadType by remember { mutableStateOf("Video") }
    
    val videoQualities = remember(metadata.formats) {
        metadata.formats
            ?.filter { !it.isAudioOnly }
            ?.mapNotNull { it.formatNote }
            ?.map { it.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }
            ?.filter { it > 0 }
            ?.distinct()
            ?.sorted()
            ?.map { "${it}p" } ?: listOf()
    }
    
    var selectedQuality by remember { mutableStateOf(videoQualities.lastOrNull() ?: "Best") }
    var fileName by remember { mutableStateOf(generateSafeFileName(metadata.title, downloadType)) }

    // Auto-update filename extension and quality when changing type
    LaunchedEffect(downloadType) {
        val baseName = fileName.substringBeforeLast(".")
        fileName = if (downloadType == "Video") {
            if (selectedQuality !in videoQualities && selectedQuality != "Best") {
                selectedQuality = videoQualities.lastOrNull() ?: "Best"
            }
            "$baseName.mp4"
        } else {
            selectedQuality = "Best Audio"
            "$baseName.mp3"
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Media Info Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AsyncImage(
                    model = metadata.thumbnail,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = metadata.title ?: "Unknown Title",
                    style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = metadata.uploader ?: metadata.channel ?: "Unknown Channel",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Violet400, fontWeight = FontWeight.Medium)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                ) {
                    val durationStr = formatDuration(metadata.duration)
                    if (durationStr.isNotEmpty()) StatusBadge(text = durationStr, color = Teal400)
                    
                    val viewsStr = formatViews(metadata.viewCount)
                    if (viewsStr.isNotEmpty()) StatusBadge(text = viewsStr, color = Violet400)
                    
                    val dateStr = formatUploadDate(metadata.uploadDate)
                    if (dateStr.isNotEmpty()) StatusBadge(text = dateStr, color = TextTertiary)
                }
                
                if (!metadata.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = metadata.description.take(150).replace("\n", " ") + "...",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 3
                    )
                }
            }
        }
        
        // Configuration Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Download Configuration",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Segmented Control for Video / Audio
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceElevated)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SegmentButton(
                        text = "MP4 Video",
                        selected = downloadType == "Video",
                        onClick = { downloadType = "Video" },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentButton(
                        text = "MP3 Audio",
                        selected = downloadType == "Audio",
                        onClick = { downloadType = "Audio" },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Quality Selection
                Text(
                    text = "Quality",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                ) {
                    if (downloadType == "Video") {
                        val displayQualities = if (videoQualities.isEmpty()) listOf("Best") else videoQualities + "Best"
                        displayQualities.forEach { q ->
                            QualityChip(
                                text = q,
                                selected = selectedQuality == q,
                                onClick = { selectedQuality = q }
                            )
                        }
                    } else {
                        QualityChip(
                            text = "Best Audio",
                            selected = true,
                            onClick = {}
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor       = TextPrimary,
                        unfocusedTextColor     = TextPrimary,
                        focusedBorderColor     = Violet500,
                        unfocusedBorderColor   = BorderSubtle,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GradientButton(
                    text = "Download",
                    onClick = {
                        val formatId = if (downloadType == "Video") {
                            if (selectedQuality == "Best") "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best"
                            else {
                                val height = selectedQuality.replace("p", "")
                                "bestvideo[height<=$height][ext=mp4]+bestaudio[ext=m4a]/best[height<=$height][ext=mp4]/best"
                            }
                        } else {
                            "audio:mp3" // Special token handled by DownloadWorker
                        }
                        
                        val estimatedSize = -1L
                        
                        downloadViewModel.startDownload(
                            url = url,
                            title = metadata.title ?: "Unknown",
                            thumbnail = metadata.thumbnail,
                            formatId = formatId,
                            estimatedSize = estimatedSize,
                            fileName = fileName
                        )
                        onDownload()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SegmentButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Violet500 else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (selected) Color.White else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
fun QualityChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Violet500.copy(alpha = 0.15f) else SurfaceElevated)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (selected) Violet400 else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
fun AnalyzeErrorState(message: String, onRetry: () -> Unit) {
    GlassCard(
        modifier     = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        glowColor    = StatusError.copy(alpha = 0.3f)
    ) {
        Column(
            modifier            = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = StatusError,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Analysis Failed",
                style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GhostButton(
                text = "Try Again",
                onClick = onRetry
            )
        }
    }
}

private fun formatDuration(seconds: Long?): String {
    if (seconds == null || seconds <= 0) return ""
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
    else String.format("%d:%02d", m, s)
}

private fun formatViews(views: Long?): String {
    if (views == null || views <= 0) return ""
    return when {
        views >= 1_000_000 -> String.format(Locale.US, "%.1fM views", views / 1_000_000.0)
        views >= 1_000 -> String.format(Locale.US, "%.1fK views", views / 1_000.0)
        else -> "$views views"
    }
}

private fun formatUploadDate(dateString: String?): String {
    if (dateString == null || dateString.length != 8) return ""
    return try {
        val inFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val outFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val date = inFormat.parse(dateString)
        if (date != null) outFormat.format(date) else ""
    } catch (e: Exception) {
        ""
    }
}

private fun generateSafeFileName(title: String?, type: String): String {
    val cleanTitle = (title ?: "Download").replace(Regex("[\\\\/:*?\"<>|]"), "_")
    return "$cleanTitle.${if (type == "Video") "mp4" else "mp3"}"
}

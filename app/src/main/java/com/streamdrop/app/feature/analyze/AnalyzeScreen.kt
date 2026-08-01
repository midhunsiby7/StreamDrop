package com.streamdrop.app.feature.analyze

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.streamdrop.app.core.data.ytdlp.YtDlpMetadata
import com.streamdrop.app.core.ui.components.*
import com.streamdrop.app.core.ui.theme.*

@Composable
fun AnalyzeScreen(
    url: String,
    onBack: () -> Unit,
    onDownload: (downloadId: Long) -> Unit,
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
        }
    }
}

@Composable
fun AnalyzeLoadingState() {
    GlassCard(
        modifier     = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
    ) {
        Column(
            modifier            = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Fetching media information...",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
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
    var selectedFormat by remember { mutableStateOf("Video (MP4)") }
    var selectedQuality by remember { mutableStateOf("1080p") }
    var fileName by remember { mutableStateOf(metadata.title ?: "download") }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Media Info Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Thumbnail
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
                    style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = metadata.uploader ?: metadata.channel ?: "Unknown Channel",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Violet400)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val durationStr = metadata.duration?.let { 
                        val mins = it / 60
                        val secs = it % 60
                        String.format("%d:%02d", mins, secs)
                    } ?: "??:??"
                    
                    StatusBadge(text = durationStr, color = Teal400)
                    if (metadata.uploadDate != null) {
                        StatusBadge(text = metadata.uploadDate, color = TextTertiary)
                    }
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
                    text = "Download Settings",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GradientButton(
                    text = "Download",
                    onClick = { 
                        downloadViewModel.startDownload(
                            url = url,
                            title = metadata.title ?: "Unknown",
                            thumbnail = metadata.thumbnail,
                            formatId = "best",
                            estimatedSize = metadata.formats?.lastOrNull()?.filesizeApprox ?: 100_000_000L,
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GhostButton(
                text = "Try Again",
                onClick = onRetry
            )
        }
    }
}

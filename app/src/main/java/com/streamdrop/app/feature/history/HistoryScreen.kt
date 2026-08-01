package com.streamdrop.app.feature.history

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.streamdrop.app.core.data.db.DownloadEntity
import com.streamdrop.app.core.data.db.DownloadStatus
import com.streamdrop.app.core.ui.components.GlassCard
import com.streamdrop.app.core.ui.theme.*
import java.io.File
import android.webkit.MimeTypeMap

import androidx.compose.material.icons.rounded.Share
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import android.provider.DocumentsContract
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun HistoryScreen(
    onOpenDownload: (downloadId: Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val downloads by viewModel.allDownloads.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .statusBarsPadding()
                .padding(padding)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                )
                
                IconButton(onClick = {
                    // Try ACTION_VIEW with a FileProvider URI
                    val streamDropDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "StreamDrop")
                    val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", streamDropDir)
                    val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "vnd.android.document/directory")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val resolveInfo = context.packageManager.resolveActivity(fallbackIntent, 0)
                    
                    if (resolveInfo != null) {
                        try {
                            context.startActivity(fallbackIntent)
                        } catch (e: Exception) {
                            val finalFallback = Intent(android.app.DownloadManager.ACTION_VIEW_DOWNLOADS)
                            finalFallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try { 
                                context.startActivity(finalFallback) 
                                scope.launch { snackbarHostState.showSnackbar("Opened Downloads. StreamDrop subfolder not supported by your file manager.") }
                            } catch (e2: Exception) {}
                        }
                    } else {
                        val finalFallback = Intent(android.app.DownloadManager.ACTION_VIEW_DOWNLOADS)
                        finalFallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try { 
                            context.startActivity(finalFallback)
                            scope.launch { snackbarHostState.showSnackbar("Opened Downloads. StreamDrop subfolder not supported by your file manager.") }
                        } catch (e: Exception) {}
                    }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = "Open Downloads Folder",
                        tint = TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Search Bar
            HistorySearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (downloads.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No download history." else "No matches found.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(downloads, key = { it.id }) { download ->
                        var showItem by remember { mutableStateOf(true) }
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Deleted ${download.title}",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            // Undo: Do nothing, item remains
                                        } else {
                                            viewModel.deleteDownload(download)
                                        }
                                    }
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                    StatusError.copy(alpha = 0.2f)
                                } else Color.Transparent
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Delete",
                                        tint = StatusError
                                    )
                                }
                            },
                            enableDismissFromStartToEnd = false
                        ) {
                            HistoryItem(
                                download = download,
                                onPlay = {
                                    if (download.status == DownloadStatus.COMPLETED) {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            val file = File(download.destinationPath)
                                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.provider",
                                                file
                                            )
                                            val extension = file.extension.lowercase()
                                            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
                                            setDataAndType(uri, mimeType)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        onOpenDownload(download.id)
                                    }
                                },
                                onShare = {
                                    if (download.status == DownloadStatus.COMPLETED) {
                                        val file = File(download.destinationPath)
                                        val uri = androidx.core.content.FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            file
                                        )
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            val extension = file.extension.lowercase()
                                            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
                                            type = mimeType
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share Video"))
                                    }
                                },
                                onDelete = {
                                    scope.launch {
                                        viewModel.deleteDownload(download)
                                        snackbarHostState.showSnackbar("Deleted ${download.title}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val animatedWidth by animateFloatAsState(
        targetValue = if (isFocused || query.isNotEmpty()) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "search_width"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth(animatedWidth)
                .onFocusChanged { isFocused = it.isFocused },
            placeholder = { Text("Search downloads...", color = TextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = TextSecondary
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Violet400,
                unfocusedBorderColor = SurfaceElevated,
                focusedContainerColor = SurfaceElevated.copy(alpha = 0.5f),
                unfocusedContainerColor = SurfaceElevated.copy(alpha = 0.5f),
                cursorColor = Violet400
            ),
            singleLine = true
        )
    }
}

@Composable
fun HistoryItem(
    download: DownloadEntity,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = download.title,
                    style = MaterialTheme.typography.labelLarge.copy(color = TextPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (download.status == DownloadStatus.FAILED && download.errorMessage != null) {
                    Text(
                        text = "Download Failed",
                        style = MaterialTheme.typography.bodySmall.copy(color = StatusError),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = download.status.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (download.status == DownloadStatus.COMPLETED) Teal400 else TextSecondary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            if (download.status == DownloadStatus.COMPLETED) {
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "Share",
                        tint = TextSecondary
                    )
                }
                IconButton(onClick = onPlay) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = Violet400
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = StatusError
                )
            }
        }
    }
}

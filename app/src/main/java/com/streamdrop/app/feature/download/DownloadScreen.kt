package com.streamdrop.app.feature.download

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.streamdrop.app.core.ui.components.GlassCard
import com.streamdrop.app.core.ui.theme.*

/**
 * DownloadScreen (Stage 1 Shell)
 *
 * Placeholder for the active download progress screen.
 * Stage 3 will replace this with real download progress,
 * animated progress bar, speed/ETA display, and controls.
 */
@Composable
fun DownloadScreen(
    downloadId: Long,
    onBack: () -> Unit,
    onGoHome: () -> Unit,
) {
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
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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
                    text  = "Downloading",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            GlassCard(
                modifier     = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text  = "Download screen coming in Stage 3",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Download ID: $downloadId",
                        style = MaterialTheme.typography.bodySmall.copy(color = Violet400),
                    )
                }
            }
        }
    }
}

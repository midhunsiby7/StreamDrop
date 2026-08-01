package com.streamdrop.app.feature.analyze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.streamdrop.app.core.ui.components.*
import com.streamdrop.app.core.ui.theme.*

/**
 * AnalyzeScreen (Stage 1 Shell)
 *
 * Placeholder screen shown after the user taps Analyze.
 * Stage 2 will replace this with actual metadata fetching and display.
 *
 * For Stage 1, this demonstrates the navigation and animation works correctly.
 */
@Composable
fun AnalyzeScreen(
    url: String,
    onBack: () -> Unit,
    onDownload: (downloadId: Long) -> Unit,
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

            // Placeholder content — Stage 2 will populate this
            GlassCard(
                modifier     = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Shimmer thumbnail placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Shimmer title
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text  = "Analyze feature coming in Stage 2",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text  = url.take(50) + if (url.length > 50) "…" else "",
                        style = MaterialTheme.typography.bodySmall.copy(color = Violet400),
                    )
                }
            }
        }
    }
}

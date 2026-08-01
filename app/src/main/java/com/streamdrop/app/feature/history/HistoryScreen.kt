package com.streamdrop.app.feature.history

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.streamdrop.app.core.ui.components.*
import com.streamdrop.app.core.ui.theme.*

/**
 * HistoryScreen (Stage 1 Shell)
 *
 * Displays the app bar, search bar UI, and an empty-state illustration.
 * Stage 5 will connect this to the Room database and populate the LazyColumn.
 */
@Composable
fun HistoryScreen(
    onOpenDownload: (downloadId: Long) -> Unit = {},
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
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text  = "Download History",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            GlassCard(
                modifier     = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint               = TextTertiary,
                        modifier           = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text  = "Search downloads…",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            GradientDivider()

            Spacer(modifier = Modifier.height(24.dp))

            // Empty state
            EmptyHistoryState()
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue  = -6f,
        targetValue   = 6f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float"
    )

    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Floating icon container
        Box(
            modifier = Modifier
                .offset(y = floatOffset.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Violet500.copy(alpha = 0.25f),
                            Teal400.copy(alpha = 0.15f),
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Rounded.Inbox,
                contentDescription = null,
                tint               = Violet400,
                modifier           = Modifier.size(48.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text       = "No downloads yet",
            style      = MaterialTheme.typography.titleLarge.copy(
                color      = TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text  = "Your completed downloads\nwill appear here",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        StatusBadge(
            text  = "Stage 5: History & DB coming soon",
            color = TextTertiary,
        )
    }
}

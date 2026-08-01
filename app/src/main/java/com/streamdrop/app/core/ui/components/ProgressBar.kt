package com.streamdrop.app.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.streamdrop.app.core.ui.theme.*

/**
 * AnimatedProgressBar
 *
 * A custom linear progress bar with a violet-to-teal gradient track
 * and a smooth animated fill. Used in the Download screen.
 *
 * @param progress    Value from 0.0 to 1.0
 * @param modifier    Standard modifier
 * @param showGlow    Whether to show the glowing end cap
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    showGlow: Boolean = true,
) {
    if (progress <= 0f) {
        LinearProgressIndicator(
            modifier = modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = Violet400,
            trackColor = Color(0xFF2A2B3A)
        )
    } else {
        val animatedProgress by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            label = "progress",
        )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2A2B3A)),
    ) {
        // Gradient fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GradientStartViolet, GradientEndTeal)
                    )
                ),
        )

        // Glow cap at the leading edge
        if (showGlow && animatedProgress > 0.02f) {
            val infiniteTransition = rememberInfiniteTransition(label = "glow")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue  = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation  = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "glow_alpha"
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.8f to Color.Transparent,
                                1.0f to Teal400.copy(alpha = glowAlpha),
                            )
                        )
                    )
            )
        }
    }
}
}

// ─── Section Divider ──────────────────────────────────────────────────────────

/**
 * Subtle gradient divider between sections.
 */
@Composable
fun GradientDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        BorderMedium,
                        Color.Transparent,
                    )
                )
            )
    )
}

// ─── Status Badge ─────────────────────────────────────────────────────────────

/**
 * Small pill-shaped status badge (e.g., "MP4", "1080p", "Completed").
 */
@Composable
fun StatusBadge(
    text: String,
    color: Color = Violet500,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text  = text,
            style = MaterialTheme.typography.labelSmall.copy(color = color),
        )
    }
}

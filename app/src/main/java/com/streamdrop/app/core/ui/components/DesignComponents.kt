package com.streamdrop.app.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.streamdrop.app.core.ui.theme.*

/**
 * GlassCard
 *
 * A frosted-glass card using a semi-transparent fill + subtle border glow.
 * Used throughout StreamDrop for content containers and info cards.
 *
 * @param modifier     Standard Compose modifier
 * @param cornerRadius Corner radius (default 20dp)
 * @param glowColor    Optional glow color (default borderGlow from design tokens)
 * @param content      Card content
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    glowColor: Color = BorderGlow,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x1AFFFFFF),   // top highlight ~10% white
                        Color(0x08FFFFFF),   // bottom fade  ~3% white
                    )
                ),
                shape = shape,
            )
            .background(
                color  = SurfaceBase.copy(alpha = 0.85f),
                shape  = shape,
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x40FFFFFF),   // top-left: bright highlight
                        glowColor,           // bottom-right: violet glow
                    )
                ),
                shape = shape,
            ),
        content = content,
    )
}

// ─── Gradient Button Background ───────────────────────────────────────────────

/**
 * Returns a linear gradient brush for the primary CTA button.
 * Violet → Teal, left to right.
 */
fun primaryGradientBrush(): Brush = Brush.horizontalGradient(
    colors = listOf(GradientStartViolet, GradientEndTeal)
)

/**
 * Returns a subtle violet radial glow brush for decorative backgrounds.
 */
fun violetGlowBrush(center: Offset = Offset.Unspecified): Brush =
    Brush.radialGradient(
        colors = listOf(
            Violet500.copy(alpha = 0.25f),
            Color.Transparent,
        ),
        center = center,
        radius = 600f,
    )

// ─── Custom soft shadow ───────────────────────────────────────────────────────

/**
 * Adds a colored drop shadow behind the composable.
 * Implements the visual depth that Compose's built-in elevation doesn't provide
 * with custom colors.
 */
fun Modifier.coloredShadow(
    color: Color = Violet500.copy(alpha = 0.35f),
    borderRadius: Dp = 20.dp,
    blurRadius: Dp = 24.dp,
    offsetY: Dp = 8.dp,
    offsetX: Dp = 0.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = android.graphics.Color.TRANSPARENT
                setShadowLayer(
                    blurRadius.toPx(),
                    offsetX.toPx(),
                    offsetY.toPx(),
                    android.graphics.Color.argb(
                        (color.alpha * 255).toInt(),
                        (color.red * 255).toInt(),
                        (color.green * 255).toInt(),
                        (color.blue * 255).toInt(),
                    )
                )
            }
        }
        canvas.drawRoundRect(
            left   = 0f,
            top    = 0f,
            right  = size.width,
            bottom = size.height,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint   = paint,
        )
    }
}

// ─── Shimmer Loading Effect ───────────────────────────────────────────────────

/**
 * Applies an animated shimmer sweep to any composable for skeleton loading states.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF2A2B3A),
                Color(0xFF3A3B55),
                Color(0xFF2A2B3A),
            ),
            start = Offset(translateAnim - 200f, 0f),
            end   = Offset(translateAnim + 200f, 0f),
        )
    )
}

// ─── Pulsing glow dot ─────────────────────────────────────────────────────────

/**
 * Animated pulsing scale modifier for live status indicators.
 */
@Composable
fun pulsingScale(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue  = 1.15f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_scale"
    )
    return scale
}

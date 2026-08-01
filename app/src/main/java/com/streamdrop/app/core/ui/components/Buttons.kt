package com.streamdrop.app.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.streamdrop.app.core.ui.theme.*

/**
 * GradientButton
 *
 * The primary CTA button for StreamDrop. Renders a violet-to-teal gradient
 * background with a subtle press-down animation.
 *
 * @param text        Button label
 * @param onClick     Click callback
 * @param modifier    Standard modifier
 * @param enabled     Whether the button is interactive
 * @param isLoading   Shows a circular progress indicator instead of text
 * @param cornerRadius Button corner radius
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    cornerRadius: Dp = 16.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "btn_scale"
    )

    val gradient = if (enabled) {
        primaryGradientBrush()
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF3A3A55),
                Color(0xFF2A3A38),
            )
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .coloredShadow(
                color        = if (enabled) Violet500.copy(alpha = 0.45f) else Color.Transparent,
                borderRadius = cornerRadius,
                blurRadius   = 20.dp,
                offsetY      = 6.dp,
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(gradient)
            .then(
                if (enabled) Modifier else Modifier.background(Color(0x55000000))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick           = onClick,
            enabled           = enabled && !isLoading,
            interactionSource = interactionSource,
            modifier          = Modifier.fillMaxWidth(),
            shape             = RoundedCornerShape(cornerRadius),
            colors            = ButtonDefaults.buttonColors(
                containerColor         = Color.Transparent,
                contentColor           = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor   = Color.White.copy(alpha = 0.4f),
            ),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
            elevation      = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier  = Modifier.size(20.dp),
                    color     = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text       = text,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp,
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}

// ─── Outlined / Secondary Button ──────────────────────────────────────────────

/**
 * GhostButton
 *
 * Secondary action button with a transparent background and a glowing
 * violet border. Used for less important actions like "Cancel" or "Clear".
 */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(100),
        label = "ghost_scale"
    )

    OutlinedButton(
        onClick           = onClick,
        enabled           = enabled,
        interactionSource = interactionSource,
        modifier          = modifier.scale(scale),
        shape             = RoundedCornerShape(16.dp),
        colors            = ButtonDefaults.outlinedButtonColors(
            contentColor          = Violet400,
            disabledContentColor  = TextTertiary,
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(
                colors = if (enabled)
                    listOf(Violet500.copy(alpha = 0.6f), Teal400.copy(alpha = 0.6f))
                else
                    listOf(BorderSubtle, BorderSubtle)
            )
        ),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp),
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.Medium,
            fontSize   = 15.sp,
        )
    }
}

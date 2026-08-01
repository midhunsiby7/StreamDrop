package com.streamdrop.app.feature.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.streamdrop.app.R
import com.streamdrop.app.core.ui.components.*
import com.streamdrop.app.core.ui.theme.*

/**
 * HomeScreen
 *
 * The entry point of StreamDrop. Displays the logo, a large URL input field,
 * a Paste button, and an Analyze CTA. A subtle animated radial glow pulses
 * behind the logo to give the interface life.
 *
 * @param onAnalyze Callback with the entered URL when Analyze is tapped
 */
@Composable
fun HomeScreen(
    onAnalyze: (url: String) -> Unit,
) {
    var urlText by remember { mutableStateOf("") }
    var urlError by remember { mutableStateOf<String?>(null) }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager       = LocalFocusManager.current

    // ── Ambient glow animation ─────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val glowRadius by infiniteTransition.animateFloat(
        initialValue  = 350f,
        targetValue   = 500f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_radius"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.10f,
        targetValue   = 0.22f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_alpha"
    )

    // ── Logo entrance animation ────────────────────────────────────────────
    var logoVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { logoVisible = true }
    val logoAlpha by animateFloatAsState(
        targetValue   = if (logoVisible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label         = "logo_alpha"
    )
    val logoScale by animateFloatAsState(
        targetValue   = if (logoVisible) 1f else 0.75f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label         = "logo_scale"
    )

    fun validateAndAnalyze() {
        val trimmed = urlText.trim()
        when {
            trimmed.isEmpty() -> urlError = "Please enter a YouTube URL"
            !trimmed.contains("youtube.com") && !trimmed.contains("youtu.be") ->
                urlError = "Only YouTube URLs are supported"
            else -> {
                urlError = null
                keyboardController?.hide()
                focusManager.clearFocus()
                onAnalyze(trimmed)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // ── Animated radial glow background ───────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush  = Brush.radialGradient(
                    colors = listOf(
                        Violet500.copy(alpha = glowAlpha),
                        Teal400.copy(alpha = glowAlpha * 0.4f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.30f),
                    radius = glowRadius,
                ),
                radius = glowRadius,
                center = Offset(size.width * 0.5f, size.height * 0.30f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // ── Logo + Wordmark ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha       = logoAlpha
                        scaleX      = logoScale
                        scaleY      = logoScale
                    },
                contentAlignment = Alignment.Center,
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Violet500.copy(alpha = 0.3f),
                                    Color.Transparent,
                                )
                            ),
                            shape = CircleShape,
                        )
                )

                // Logo icon circle
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .coloredShadow(
                            color        = Violet500.copy(alpha = 0.5f),
                            borderRadius = 44.dp,
                            blurRadius   = 30.dp,
                            offsetY      = 10.dp,
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Violet600, Teal400),
                                start  = Offset(0f, 0f),
                                end    = Offset(200f, 200f),
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.Download,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(40.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name with gradient text effect
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Violet400, Teal400)
                        ),
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )) {
                        append("Stream")
                    }
                    withStyle(SpanStyle(
                        color      = TextPrimary,
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )) {
                        append("Drop")
                    }
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text  = "Download anything, beautifully",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
            )

            Spacer(modifier = Modifier.height(56.dp))

            // ── URL Input Card ─────────────────────────────────────────────
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text  = "YouTube URL",
                        style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input field row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedTextField(
                            value         = urlText,
                            onValueChange = {
                                urlText   = it
                                urlError  = null
                            },
                            modifier      = Modifier.weight(1f),
                            placeholder   = {
                                Text(
                                    text  = stringResource(R.string.url_input_hint),
                                    color = TextTertiary,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            leadingIcon   = {
                                Icon(
                                    imageVector        = Icons.Rounded.Link,
                                    contentDescription = null,
                                    tint               = if (urlText.isNotEmpty()) Violet400 else TextTertiary,
                                )
                            },
                            trailingIcon  = {
                                AnimatedVisibility(visible = urlText.isNotEmpty()) {
                                    IconButton(onClick = { urlText = ""; urlError = null }) {
                                        Icon(
                                            imageVector        = Icons.Rounded.Clear,
                                            contentDescription = stringResource(R.string.btn_clear),
                                            tint               = TextTertiary,
                                        )
                                    }
                                }
                            },
                            isError       = urlError != null,
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction    = ImeAction.Go,
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = { validateAndAnalyze() }
                            ),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedTextColor       = TextPrimary,
                                unfocusedTextColor     = TextPrimary,
                                focusedBorderColor     = Violet500,
                                unfocusedBorderColor   = BorderSubtle,
                                errorBorderColor       = StatusError,
                                focusedContainerColor  = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor            = Violet400,
                            ),
                            shape         = RoundedCornerShape(14.dp),
                        )
                    }

                    // Error message
                    AnimatedVisibility(
                        visible = urlError != null,
                        enter   = fadeIn() + expandVertically(),
                        exit    = fadeOut() + shrinkVertically(),
                    ) {
                        urlError?.let { error ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier          = Modifier.padding(top = 8.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Rounded.ErrorOutline,
                                    contentDescription = null,
                                    tint               = StatusError,
                                    modifier           = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text  = error,
                                    style = MaterialTheme.typography.bodySmall.copy(color = StatusError),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Paste + Analyze buttons
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Paste button
                        GhostButton(
                            text     = stringResource(R.string.btn_paste),
                            onClick  = {
                                val clip = clipboardManager.getText()?.text ?: ""
                                if (clip.isNotEmpty()) {
                                    urlText  = clip
                                    urlError = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )

                        // Analyze CTA
                        GradientButton(
                            text     = stringResource(R.string.btn_analyze),
                            onClick  = { validateAndAnalyze() },
                            modifier = Modifier.weight(2f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Feature Hint Row ───────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FeatureHintChip(icon = Icons.Rounded.VideoFile,  label = "MP4 Video")
                FeatureHintChip(icon = Icons.Rounded.AudioFile,  label = "MP3 Audio")
                FeatureHintChip(icon = Icons.Rounded.HighQuality, label = "4K / 1080p")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Small feature hint chips displayed below the input ───────────────────────

@Composable
private fun RowScope.FeatureHintChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
) {
    GlassCard(
        modifier     = Modifier.weight(1f),
        cornerRadius = 14.dp,
        glowColor    = Violet500.copy(alpha = 0.2f),
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = Violet400,
                modifier           = Modifier.size(20.dp),
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
            )
        }
    }
}

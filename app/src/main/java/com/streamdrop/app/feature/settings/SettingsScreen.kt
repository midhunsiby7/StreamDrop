package com.streamdrop.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.streamdrop.app.core.ui.components.*
import com.streamdrop.app.core.ui.theme.*

/**
 * SettingsScreen (Stage 1 Shell)
 *
 * Shows the settings UI layout with real toggle and selector components.
 * DataStore persistence will be connected in Stage 5.
 */
@Composable
fun SettingsScreen() {
    var darkModeEnabled by remember { mutableStateOf(true) }
    var defaultQuality  by remember { mutableStateOf("1080p") }
    var defaultFormat   by remember { mutableStateOf("MP4") }
    var maxConcurrent   by remember { mutableStateOf(2) }

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
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text  = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Appearance Section ─────────────────────────────────────────
            SettingsSectionHeader(title = "Appearance", icon = Icons.Rounded.Palette)

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(
                modifier     = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    SettingsToggleRow(
                        icon     = Icons.Rounded.DarkMode,
                        title    = "Dark Mode",
                        subtitle = "Use dark theme throughout the app",
                        checked  = darkModeEnabled,
                        onToggle = { darkModeEnabled = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Downloads Section ──────────────────────────────────────────
            SettingsSectionHeader(title = "Downloads", icon = Icons.Rounded.Download)

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(
                modifier     = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    SettingsChooserRow(
                        icon     = Icons.Rounded.HighQuality,
                        title    = "Default Quality",
                        subtitle = "Applied when quick-downloading",
                        value    = defaultQuality,
                        options  = listOf("4K", "1080p", "720p", "480p", "360p"),
                        onSelect = { defaultQuality = it },
                    )

                    GradientDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsChooserRow(
                        icon     = Icons.Rounded.VideoFile,
                        title    = "Default Format",
                        subtitle = "MP4 for video, MP3 for audio",
                        value    = defaultFormat,
                        options  = listOf("MP4", "MP3"),
                        onSelect = { defaultFormat = it },
                    )

                    GradientDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsStepperRow(
                        icon     = Icons.Rounded.Queue,
                        title    = "Max Concurrent Downloads",
                        subtitle = "Recommended: 2",
                        value    = maxConcurrent,
                        range    = 1..5,
                        onStep   = { maxConcurrent = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Danger Zone ────────────────────────────────────────────────
            SettingsSectionHeader(title = "Data", icon = Icons.Rounded.Storage)

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(
                modifier     = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                glowColor    = StatusError.copy(alpha = 0.25f),
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StatusError.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.DeleteSweep,
                            contentDescription = null,
                            tint               = StatusError,
                            modifier           = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text  = "Clear Download History",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color      = TextPrimary,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                        Text(
                            text  = "Permanently removes all records",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary),
                        )
                    }
                    GhostButton(text = "Clear", onClick = { /* Stage 5 */ })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App version
            Text(
                text  = "StreamDrop v1.0.0 · Stage 1",
                style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Violet400,
            modifier           = Modifier.size(18.dp),
        )
        Text(
            text  = title.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                color        = Violet400,
                fontWeight   = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
            ),
        )
    }
}

// ─── Toggle Row ───────────────────────────────────────────────────────────────

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconBox(icon = icon)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary, fontWeight = FontWeight.Medium))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor       = TextOnPrimary,
                checkedTrackColor       = Violet500,
                uncheckedThumbColor     = TextSecondary,
                uncheckedTrackColor     = SurfaceElevated,
            ),
        )
    }
}

// ─── Chooser Row ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsChooserRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconBox(icon = icon)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,    style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary, fontWeight = FontWeight.Medium))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(text = value, color = Violet400, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Violet400)
            }
            DropdownMenu(
                expanded        = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text    = { Text(opt) },
                        onClick = { onSelect(opt); expanded = false },
                    )
                }
            }
        }
    }
}

// ─── Stepper Row ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsStepperRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: Int,
    range: IntRange,
    onStep: (Int) -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconBox(icon = icon)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,    style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary, fontWeight = FontWeight.Medium))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (value > range.first) onStep(value - 1) },
                enabled = value > range.first,
            ) {
                Icon(Icons.Rounded.Remove, contentDescription = "Decrease", tint = if (value > range.first) Violet400 else TextTertiary)
            }
            Text(
                text  = value.toString(),
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary),
            )
            IconButton(
                onClick = { if (value < range.last) onStep(value + 1) },
                enabled = value < range.last,
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Increase", tint = if (value < range.last) Violet400 else TextTertiary)
            }
        }
    }
}

// ─── Icon Box ─────────────────────────────────────────────────────────────────

@Composable
private fun SettingsIconBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Violet500.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Violet400,
            modifier           = Modifier.size(20.dp),
        )
    }
}



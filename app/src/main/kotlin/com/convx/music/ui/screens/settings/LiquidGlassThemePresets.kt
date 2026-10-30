/**
 * Aurora theme presets built on the existing Liquid Glass engine.
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.convx.music.constants.LiquidGlassBlurRadiusKey
import com.convx.music.constants.LiquidGlassGlobalEnabledKey
import com.convx.music.constants.LiquidGlassMiniPlayerEnabledKey
import com.convx.music.constants.LiquidGlassNavBarEnabledKey
import com.convx.music.constants.LiquidGlassStyleKey
import com.convx.music.constants.LiquidGlassSurfaceOpacityKey
import com.convx.music.constants.LiquidGlassVibrancyKey
import com.convx.music.ui.component.GlassStyle
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.theme.ChillaxFontFamily
import com.convx.music.ui.utils.bounceClick
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlin.math.abs

private data class GlassPreset(
    val title: String,
    val description: String,
    val style: GlassStyle,
    val blurRadius: Float,
    val vibrancy: Float,
    val surfaceOpacity: Float,
    val swatch: List<Color>,
)

private val LiquidGlassPreset = GlassPreset(
    title = "Liquid Glass",
    description = "Kaca bening dengan refraksi lensa dan tepi berkilau.",
    style = GlassStyle.LIQUID,
    blurRadius = 3f,
    vibrancy = 2.0f,
    surfaceOpacity = 0.45f,
    swatch = listOf(Color(0xFF9AD8FF), Color(0xFF6C7BFF)),
)

private val PremiumBlurPreset = GlassPreset(
    title = "Liquid Glass Premium Blur",
    description = "Blur lembut yang lebih tebal untuk tampilan mewah dan tenang.",
    style = GlassStyle.LIQUID,
    blurRadius = 28f,
    vibrancy = 2.4f,
    surfaceOpacity = 0.62f,
    swatch = listOf(Color(0xFFE9D7FF), Color(0xFF8F6BFF)),
)

/** Two one-tap theme presets that configure every Liquid Glass surface at once. */
@Composable
fun LiquidGlassThemePresets(modifier: Modifier = Modifier.fillMaxWidth(0.9f)) {
    val (globalEnabled, onGlobalEnabledChange) = rememberPreference(LiquidGlassGlobalEnabledKey, defaultValue = true)
    val (style, onStyleChange) = rememberEnumPreference(LiquidGlassStyleKey, defaultValue = GlassStyle.LIQUID)
    val (blurRadius, onBlurRadiusChange) = rememberPreference(LiquidGlassBlurRadiusKey, defaultValue = 3f)
    val (_, onVibrancyChange) = rememberPreference(LiquidGlassVibrancyKey, defaultValue = 2.0f)
    val (_, onSurfaceOpacityChange) = rememberPreference(LiquidGlassSurfaceOpacityKey, defaultValue = 0.5f)
    val (_, onMiniPlayerChange) = rememberPreference(LiquidGlassMiniPlayerEnabledKey, defaultValue = true)
    val (_, onNavBarChange) = rememberPreference(LiquidGlassNavBarEnabledKey, defaultValue = true)

    fun apply(preset: GlassPreset) {
        onGlobalEnabledChange(true)
        onStyleChange(preset.style)
        onBlurRadiusChange(preset.blurRadius)
        onVibrancyChange(preset.vibrancy)
        onSurfaceOpacityChange(preset.surfaceOpacity)
        onMiniPlayerChange(true)
        onNavBarChange(true)
    }

    fun isActive(preset: GlassPreset) =
        globalEnabled && style == preset.style && abs(blurRadius - preset.blurRadius) < 0.5f

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Tema Aurora",
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface,
        )
        listOf(LiquidGlassPreset, PremiumBlurPreset).forEach { preset ->
            GlassPresetCard(
                preset = preset,
                selected = isActive(preset),
                onClick = { apply(preset) },
            )
        }
    }
}

@Composable
private fun GlassPresetCard(preset: GlassPreset, selected: Boolean, onClick: () -> Unit) {
    val onSurface = AppTheme.colorScheme.onSurface
    val shape = RoundedCornerShape(22.dp)
    val borderColor = if (selected) AppTheme.colorScheme.primary else onSurface.copy(alpha = 0.12f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(onSurface.copy(alpha = 0.09f), onSurface.copy(alpha = 0.03f))
                )
            )
            .border(if (selected) 1.5.dp else 1.dp, borderColor, shape)
            .bounceClick(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(preset.swatch))
                .border(1.dp, Color.White.copy(alpha = 0.55f), CircleShape)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = preset.title,
                fontFamily = ChillaxFontFamily,
                style = AppTheme.typography.titleMedium,
                color = onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = preset.description,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primary)
            )
        }
    }
}

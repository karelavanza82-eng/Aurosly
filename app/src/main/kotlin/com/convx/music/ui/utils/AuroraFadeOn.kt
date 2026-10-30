/**
 * Aurora scroll motion.
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * "Fade On" entrance for list and grid rows. Lazy lists attach a row's node every time the row
 * scrolls into view -- in either direction -- so the fade replays on every scroll, not only the
 * first time a screen opens.
 *
 * Runs entirely in the row's graphics layer (alpha, a short rise and a hint of scale), so it
 * never re-measures, re-lays-out or recomposes the row and stays smooth on low-end devices.
 */
fun Modifier.auroraFadeOn(): Modifier = this then AuroraFadeOnElement

private object AuroraFadeOnElement : ModifierNodeElement<AuroraFadeOnNode>() {
    override fun create(): AuroraFadeOnNode = AuroraFadeOnNode()
    override fun update(node: AuroraFadeOnNode) = Unit
    override fun InspectorInfo.inspectableProperties() {
        name = "auroraFadeOn"
    }
    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = "auroraFadeOn".hashCode()
}

private class AuroraFadeOnNode : Modifier.Node(), LayoutModifierNode {
    private val progress = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMillis = FADE_DURATION_MS, easing = FadeEasing))
        }
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                val p = progress.value
                alpha = p
                translationY = (1f - p) * RISE_DP.dp.toPx()
                val s = START_SCALE + (1f - START_SCALE) * p
                scaleX = s
                scaleY = s
            }
        }
    }
}

private const val FADE_DURATION_MS = 420
private const val RISE_DP = 14
private const val START_SCALE = 0.97f
private val FadeEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

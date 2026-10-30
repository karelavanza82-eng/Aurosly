/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 * Modified for Aurora by muhamadkarel.
 */

package com.convx.music.ui.component

import android.provider.Settings
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlin.math.min
import kotlin.math.pow

/**
 * Cold-start intro for Aurora: a single line coils around like a snake to draw the logo's ring,
 * then traces the outline of every paw pad, and the pads settle into the solid logo. Only the
 * logo's own lines move -- no glow, particles or background box -- and the overlay fades onto
 * the live app underneath.
 *
 * One frame clock drives every layer and only the draw phase reads it, so the animation never
 * recomposes and stays at the display refresh rate. Each frame step is capped at [MAX_STEP_MS]
 * so a startup stall pauses the intro instead of skipping it. Skipped entirely when the system
 * animator scale is 0 (reduced motion).
 */
@Composable
fun ConvxIntro(onFinished: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    val reducedMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val clock = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        if (!reducedMotion) {
            var last = withFrameNanos { it }
            var ticked = false
            while (clock.floatValue < TOTAL_MS) {
                val now = withFrameNanos { it }
                val step = ((now - last) / 1e6f).coerceAtMost(MAX_STEP_MS)
                last = now
                clock.floatValue = (clock.floatValue + step).coerceAtMost(TOTAL_MS)
                if (!ticked && clock.floatValue >= RING_END_MS) {
                    ticked = true
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            }
        }
        onFinished()
    }
    if (reducedMotion) return

    val shapes = remember { AuroraLogoShapes() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val t = clock.floatValue
                alpha = 1f - easeInOut(progress(t, FADE_START_MS, TOTAL_MS))
            }
            .background(BACKGROUND)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val t = clock.floatValue
            val logoSize = min(size.width, size.height) * LOGO_FRACTION
            val unit = logoSize / VIEWPORT
            val origin = Offset((size.width - logoSize) / 2f, (size.height - logoSize) / 2f)

            translate(origin.x, origin.y) {
                scale(unit, unit, pivot = Offset.Zero) {
                    // 1. The ring: a line slithering around while it grows into a full circle.
                    val ring = easeInOut(progress(t, 0f, RING_END_MS))
                    if (ring > 0f) {
                        val sweep = 360f * ring
                        val start = -90f - 140f * (1f - ring)
                        drawArc(
                            color = LINE,
                            startAngle = start,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(RING_CENTER - RING_RADIUS, RING_CENTER - RING_RADIUS),
                            size = Size(RING_RADIUS * 2f, RING_RADIUS * 2f),
                            style = Stroke(
                                width = RING_THICKNESS,
                                cap = if (ring < 1f) StrokeCap.Round else StrokeCap.Butt,
                            ),
                        )
                    }

                    // 2. Each pad's outline is traced, staggered toe by toe.
                    val outline = Stroke(width = OUTLINE_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    shapes.pads.forEachIndexed { index, pad ->
                        val begin = TRACE_START_MS + index * TRACE_STAGGER_MS
                        val p = easeInOut(progress(t, begin, begin + TRACE_DURATION_MS))
                        if (p <= 0f) return@forEachIndexed
                        if (p >= 1f) {
                            drawPath(pad.full, LINE, style = outline)
                        } else {
                            pad.segment.reset()
                            pad.measure.getSegment(0f, pad.length * p, pad.segment, true)
                            drawPath(pad.segment, LINE, style = outline)
                        }
                    }

                    // 3. The traced pads settle into the solid logo.
                    val fill = easeInOut(progress(t, FILL_START_MS, FILL_END_MS))
                    if (fill > 0f) {
                        shapes.pads.forEach { drawPath(it.full, LINE.copy(alpha = fill)) }
                    }
                }
            }
        }
    }
}

/** The paw pads of the Aurora logo in a 100x100 viewport, measured once for line tracing. */
private class AuroraLogoShapes {
    class Pad(val full: Path) {
        val measure = PathMeasure().apply { setPath(full, true) }
        val length = measure.length
        val segment = Path()
    }

    val pads: List<Pad> = PAD_PATHS.map { Pad(PathParser().parsePathString(it).toPath()) }
}

private fun progress(t: Float, start: Float, end: Float): Float =
    ((t - start) / (end - start)).coerceIn(0f, 1f)

private fun easeInOut(x: Float): Float =
    if (x < 0.5f) 4f * x * x * x else 1f - (-2f * x + 2f).pow(3) / 2f

private val BACKGROUND = Color.Black
private val LINE = Color.White

private const val VIEWPORT = 100f
private const val LOGO_FRACTION = 0.46f
private const val RING_CENTER = 50f
private const val RING_RADIUS = 32.5f
private const val RING_THICKNESS = 5.6f
private const val OUTLINE_WIDTH = 1.4f

private const val RING_END_MS = 1250f
private const val TRACE_START_MS = 650f
private const val TRACE_STAGGER_MS = 110f
private const val TRACE_DURATION_MS = 900f
private const val FILL_START_MS = 1750f
private const val FILL_END_MS = 2150f
private const val FADE_START_MS = 2550f
private const val TOTAL_MS = 2950f
private const val MAX_STEP_MS = 34f

// Toes left to right, then the main pad -- same geometry as res/drawable/convx_logo.xml.
private val PAD_PATHS = listOf(
    "M30.26,41.76 C32.87,40.55 36.11,42.00 37.51,45.00 C38.91,48.01 37.94,51.42 35.34,52.64 C32.73,53.85 29.49,52.40 28.09,49.40 C26.69,46.39 27.66,42.98 30.26,41.76 Z",
    "M41.64,30.07 C44.76,29.63 47.71,32.33 48.24,36.11 C48.77,39.88 46.68,43.29 43.56,43.73 C40.44,44.17 37.49,41.47 36.96,37.69 C36.43,33.92 38.52,30.51 41.64,30.07 Z",
    "M58.17,30.67 C61.24,31.10 63.28,34.55 62.75,38.38 C62.21,42.21 59.29,44.96 56.23,44.53 C53.16,44.10 51.12,40.65 51.65,36.82 C52.19,32.99 55.11,30.24 58.17,30.67 Z",
    "M69.70,43.08 C72.28,44.57 73.08,48.03 71.48,50.80 C69.87,53.57 66.48,54.61 63.90,53.12 C61.32,51.63 60.52,48.17 62.12,45.40 C63.73,42.63 67.12,41.59 69.70,43.08 Z",
    "M49.60,45.70 C54.00,45.70 57.00,47.80 59.00,51.20 C61.00,54.50 63.60,58.00 63.60,61.00 C63.60,64.80 60.80,67.20 57.00,67.20 C54.00,67.20 52.00,64.30 49.60,64.30 C47.40,64.30 45.50,66.90 42.40,66.90 C38.60,66.90 35.90,64.40 35.90,61.00 C35.90,58.00 37.60,55.00 39.60,51.60 C41.80,47.80 45.20,45.70 49.60,45.70 Z",
)

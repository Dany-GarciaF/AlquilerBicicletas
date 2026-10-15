package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Direction from which a [Reveal] travels into place. */
enum class RevealFrom { BOTTOM, LEFT, RIGHT, SCALE }

/** Decelerates strongly at the end, which reads as a soft, natural landing. */
private val EaseOutExpo = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

/**
 * Fades, travels and slightly scales its content in the first time it enters the composition.
 * Inside a lazy list this happens as the section scrolls into view; [delayMillis] staggers siblings.
 * Opacity settles faster than movement so the content appears before it finishes moving.
 */
@Composable
fun Reveal(
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    from: RevealFrom = RevealFrom.BOTTOM,
    distance: Dp = 56.dp,
    content: @Composable () -> Unit,
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val target = if (shown) 1f else 0f
    val motion by animateFloatAsState(target, tween(900, delayMillis, EaseOutExpo), label = "reveal-motion")
    val alpha by animateFloatAsState(target, tween(550, delayMillis, EaseOutExpo), label = "reveal-alpha")
    Box(
        modifier.graphicsLayer {
            this.alpha = alpha
            val remaining = 1f - motion
            val travel = distance.toPx() * remaining
            when (from) {
                RevealFrom.BOTTOM -> translationY = travel
                RevealFrom.LEFT -> translationX = -travel
                RevealFrom.RIGHT -> translationX = travel
                RevealFrom.SCALE -> Unit
            }
            val scale = 1f - 0.06f * remaining
            scaleX = scale
            scaleY = scale
        },
    ) { content() }
}
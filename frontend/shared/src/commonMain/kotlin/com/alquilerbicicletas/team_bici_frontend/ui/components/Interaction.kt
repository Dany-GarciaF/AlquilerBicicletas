package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/** Smoothly enlarges the element while the pointer hovers over it. */
@Composable
fun Modifier.hoverScale(scale: Float = 1.03f): Modifier {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val current by animateFloatAsState(if (hovered) scale else 1f, spring(dampingRatio = 0.6f, stiffness = 380f), label = "hover-scale")
    return this
        .graphicsLayer {
            scaleX = current
            scaleY = current
        }
        .hoverable(interaction)
}
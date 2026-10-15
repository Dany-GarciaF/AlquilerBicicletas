package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.ui.theme.extendedColors

/** Surface card. With [lift] it reacts to the pointer: it grows slightly, gains a shadow and an accent border. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    lift: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val active = lift && hovered
    val scale by animateFloatAsState(if (active) 1.025f else 1f, tween(180), label = "card-scale")
    val elevation by animateDpAsState(if (active) 14.dp else 0.dp, tween(180), label = "card-elevation")
    val borderColor by animateColorAsState(
        if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        tween(180),
        label = "card-border",
    )
    Card(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .hoverable(interaction, enabled = lift),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(if (active) 1.5.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        content = content,
    )
}

/** Tonos disponibles para las insignias de estado. */
enum class BadgeTone { SUCCESS, WARNING, DANGER, ACCENT, NEUTRAL }

@Composable
fun StatusBadge(
    text: String,
    tone: BadgeTone,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val extended = MaterialTheme.extendedColors
    val (container, content) = when (tone) {
        BadgeTone.SUCCESS -> extended.successContainer to extended.onSuccessContainer
        BadgeTone.WARNING -> extended.warningContainer to extended.onWarningContainer
        BadgeTone.DANGER -> scheme.errorContainer to scheme.onErrorContainer
        BadgeTone.ACCENT -> scheme.primaryContainer to scheme.onPrimaryContainer
        BadgeTone.NEUTRAL -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
    Text(
        text = text,
        color = content,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(container, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

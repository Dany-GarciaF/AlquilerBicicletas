package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role

/** Location marker. When [onClick] is set it grows on hover, shows a hand cursor and is clickable. */
@Composable
fun MapPin(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val interactive = if (onClick != null) {
        Modifier
            .hoverScale(1.18f)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(role = Role.Button, onClick = onClick)
    } else {
        Modifier
    }
    Canvas(modifier.then(interactive)) {
        val radius = size.width * 0.34f
        val center = Offset(size.width / 2, radius + size.height * 0.04f)
        val tip = Offset(size.width / 2, size.height * 0.96f)
        drawCircle(color, radius, center)
        val path = Path().apply {
            moveTo(center.x - radius * 0.86f, center.y + radius * 0.5f)
            lineTo(tip.x, tip.y)
            lineTo(center.x + radius * 0.86f, center.y + radius * 0.5f)
            close()
        }
        drawPath(path, color)
        drawCircle(Color.White, radius * 0.38f, center)
    }
}

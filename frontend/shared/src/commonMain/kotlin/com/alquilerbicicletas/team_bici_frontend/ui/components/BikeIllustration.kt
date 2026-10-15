package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Line-art bicycle used as a visual until real photos are available. */
@Composable
fun BikeIllustration(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
) {
    Canvas(modifier = modifier) {
        val stroke = strokeWidth.toPx()
        val radius = size.height * 0.28f
        val centerY = size.height * 0.64f
        val rear = Offset(size.width * 0.30f, centerY)
        val front = Offset(size.width * 0.70f, centerY)
        val bottomBracket = Offset(size.width * 0.48f, centerY)
        val seat = Offset(size.width * 0.44f, centerY - radius * 1.25f)
        val head = Offset(size.width * 0.64f, centerY - radius * 1.3f)

        drawCircle(color, radius, rear, style = Stroke(stroke))
        drawCircle(color, radius, front, style = Stroke(stroke))
        listOf(
            rear to bottomBracket,
            bottomBracket to seat,
            seat to head,
            head to front,
            bottomBracket to head,
            rear to seat,
        ).forEach { (from, to) ->
            drawLine(color, from, to, strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }
}

package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.ui.components.Reveal
import com.alquilerbicicletas.team_bici_frontend.ui.components.RevealFrom
import com.alquilerbicicletas.team_bici_frontend.ui.maps.MapLinks
import com.alquilerbicicletas.team_bici_frontend.ui.theme.BrandGradient
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

private val RouteTips = listOf(
    "Activa la capa de bicicleta en Google Maps para ver ciclorrutas y vías aptas para pedalear.",
    "Planea tu trayecto en modo bicicleta desde la estación donde retiras.",
    "Usa casco, luces y respeta la señalización: tu seguridad va primero.",
)

/** Orange band that points riders to the cycling layer of Google Maps. */
@Composable
fun RoutesSection() {
    val wide = LocalWindowSize.current != WindowSize.COMPACT
    val uriHandler = LocalUriHandler.current
    SectionContainer(background = BrandGradient, verticalPadding = 64.dp) {
        val copy: @Composable (Modifier) -> Unit = { modifier ->
            Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Conoce las ciclorrutas de tu ciudad",
                    color = Color.White,
                    fontSize = if (wide) 38.sp else 28.sp,
                    lineHeight = if (wide) 42.sp else 32.sp,
                    fontWeight = FontWeight.Black,
                )
                RouteTips.forEach { tip ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.padding(top = 8.dp).size(8.dp).background(Color.White, CircleShape))
                        Text(tip, color = Color.White.copy(alpha = 0.94f), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { uriHandler.openUri(MapLinks.bikeLayer()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF14161A)),
                    ) { Text("Ver ciclorrutas") }
                    OutlinedButton(
                        onClick = { uriHandler.openUri(MapLinks.planRoute()) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.5.dp, Color.White),
                    ) { Text("Planear mi ruta") }
                }
            }
        }
        if (wide) {
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp), verticalAlignment = Alignment.CenterVertically) {
                Reveal(Modifier.weight(1f), from = RevealFrom.LEFT) { copy(Modifier.fillMaxWidth()) }
                Reveal(Modifier.weight(1f), delayMillis = 200, from = RevealFrom.SCALE) {
                    RoutesIllustration(Modifier.fillMaxWidth().height(340.dp))
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                Reveal { copy(Modifier.fillMaxWidth()) }
                Reveal(delayMillis = 150, from = RevealFrom.SCALE) { RoutesIllustration(Modifier.fillMaxWidth().height(240.dp)) }
            }
        }
    }
}

/** Dark card with animated, flowing route lines connecting a few stops. */
@Composable
private fun RoutesIllustration(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "routes")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "routes-phase",
    )
    Canvas(modifier.clip(RoundedCornerShape(28.dp)).background(Color(0xFF14161A))) {
        val w = size.width
        val h = size.height
        val grid = Color.White.copy(alpha = 0.05f)
        val step = 40.dp.toPx()
        var gx = step
        while (gx < w) { drawLine(grid, Offset(gx, 0f), Offset(gx, h), 1.dp.toPx()); gx += step }
        var gy = step
        while (gy < h) { drawLine(grid, Offset(0f, gy), Offset(w, gy), 1.dp.toPx()); gy += step }

        val routes = listOf(
            listOf(0.08f to 0.78f, 0.30f to 0.62f, 0.52f to 0.66f, 0.78f to 0.32f, 0.94f to 0.24f),
            listOf(0.12f to 0.20f, 0.32f to 0.34f, 0.52f to 0.66f, 0.70f to 0.84f, 0.92f to 0.80f),
        )
        val dash = 18.dp.toPx()
        routes.forEachIndexed { index, points ->
            val path = Path().apply {
                points.forEachIndexed { i, (px, py) ->
                    if (i == 0) moveTo(px * w, py * h) else lineTo(px * w, py * h)
                }
            }
            drawPath(
                path,
                color = if (index == 0) Color(0xFFFF7A3D) else Color(0xFFFFC27A),
                style = Stroke(
                    width = 5.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash * 0.8f), -phase * dash * 1.8f),
                ),
            )
        }
        routes.flatten().distinct().forEach { (px, py) -> stop(Offset(px * w, py * h)) }
    }
}

private fun DrawScope.stop(center: Offset) {
    drawCircle(Color(0xFFFF7A3D).copy(alpha = 0.25f), 14.dp.toPx(), center)
    drawCircle(Color.White, 6.dp.toPx(), center)
}

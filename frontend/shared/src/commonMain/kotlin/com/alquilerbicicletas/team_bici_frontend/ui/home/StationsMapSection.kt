package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogState
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
import com.alquilerbicicletas.team_bici_frontend.ui.components.MapPin
import com.alquilerbicicletas.team_bici_frontend.ui.components.Reveal
import com.alquilerbicicletas.team_bici_frontend.ui.components.RevealFrom
import com.alquilerbicicletas.team_bici_frontend.ui.components.SkeletonCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.StatusBadge
import com.alquilerbicicletas.team_bici_frontend.ui.maps.MapLinks
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize
import kotlin.math.roundToInt

/** Interactive schematic map of the stations: every pin opens that station in Google Maps. */
@Composable
fun StationsMapSection(catalog: CatalogState, onSeeStations: () -> Unit) {
    val wide = LocalWindowSize.current != WindowSize.COMPACT
    val uriHandler = LocalUriHandler.current

    SectionContainer(verticalPadding = 48.dp) {
        Reveal {
            SectionTitle(
                title = "Revisa dónde están nuestras estaciones",
                subtitle = "Toca un marcador para abrir la estación en Google Maps y planear cómo llegar en bicicleta.",
            )
        }
        Spacer(Modifier.height(24.dp))
        if (wide) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Reveal(Modifier.weight(1.7f), delayMillis = 100, from = RevealFrom.LEFT) {
                    StationsMap(catalog, Modifier.height(420.dp)) { uriHandler.openUri(MapLinks.place(it)) }
                }
                Reveal(Modifier.weight(1f), delayMillis = 250, from = RevealFrom.RIGHT) {
                    StationList(catalog, Modifier, onSeeStations) { uriHandler.openUri(MapLinks.directions(it)) }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Reveal(delayMillis = 100, from = RevealFrom.SCALE) {
                    StationsMap(catalog, Modifier.height(300.dp)) { uriHandler.openUri(MapLinks.place(it)) }
                }
                Reveal(delayMillis = 200) {
                    StationList(catalog, Modifier, onSeeStations) { uriHandler.openUri(MapLinks.directions(it)) }
                }
            }
        }
    }
}

@Composable
private fun StationsMap(catalog: CatalogState, modifier: Modifier, onOpen: (EstacionResponseDto) -> Unit) {
    if (catalog.showSkeleton) {
        SkeletonCard(modifier, height = 300.dp)
        return
    }
    val stations = catalog.estaciones
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        MapBackdrop(Modifier.fillMaxSize())
        if (stations.isEmpty()) {
            Text(
                "Aún no hay estaciones registradas.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            val minLat = stations.minOf { it.latitud }
            val maxLat = stations.maxOf { it.latitud }
            val minLon = stations.minOf { it.longitud }
            val maxLon = stations.maxOf { it.longitud }
            BoxWithConstraints(Modifier.fillMaxSize()) {
                stations.forEach { estacion ->
                    val x = project(estacion.longitud, minLon, maxLon, 0.14f, 0.86f)
                    val y = project(maxLat - estacion.latitud + minLat, minLat, maxLat, 0.26f, 0.86f)
                    StationMarker(
                        estacion = estacion,
                        available = catalog.availableAt(estacion.id),
                        onClick = { onOpen(estacion) },
                        modifier = Modifier.offset {
                            IntOffset(
                                (x * constraints.maxWidth).roundToInt() - 20.dp.roundToPx(),
                                (y * constraints.maxHeight).roundToInt() - 48.dp.roundToPx(),
                            )
                        },
                    )
                }
            }
        }
        Text(
            "Mapa esquemático · Google Maps al tocar un marcador",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

/** Normalizes [value] into [[outMin], [outMax]]; a single point is centered. */
private fun project(value: Double, min: Double, max: Double, outMin: Float, outMax: Float): Float {
    if (max - min < 1e-9) return (outMin + outMax) / 2f
    return outMin + ((value - min) / (max - min)).toFloat() * (outMax - outMin)
}

@Composable
private fun StationMarker(
    estacion: EstacionResponseDto,
    available: Int,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(modifier.zIndex(if (hovered) 2f else 0f).size(width = 40.dp, height = 48.dp).hoverable(interaction)) {
        MapPin(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxSize(), onClick = onClick)
        if (hovered) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .wrapContentSize(align = Alignment.BottomCenter, unbounded = true)
                    .offset(y = (-54).dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 6.dp,
            ) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(estacion.nombre, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.inverseOnSurface)
                    Text(
                        "$available disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.8f),
                    )
                }
            }
        }
    }
}

/** Decorative city backdrop: river, parks and a street grid with a couple of main avenues. */
@Composable
private fun MapBackdrop(modifier: Modifier) {
    val street = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val step = 56.dp.toPx()
        var x = step / 2
        while (x < w) {
            drawLine(street, Offset(x, 0f), Offset(x, h), strokeWidth = 2.dp.toPx())
            x += step
        }
        var y = step / 2
        while (y < h) {
            drawLine(street, Offset(0f, y), Offset(w, y), strokeWidth = 2.dp.toPx())
            y += step
        }
        drawLine(street, Offset(0f, h * 0.9f), Offset(w, h * 0.15f), strokeWidth = 9.dp.toPx())
        drawLine(street, Offset(w * 0.2f, 0f), Offset(w * 0.75f, h), strokeWidth = 7.dp.toPx())
        val river = Path().apply {
            moveTo(0f, h * 0.3f)
            cubicTo(w * 0.25f, h * 0.1f, w * 0.4f, h * 0.55f, w * 0.65f, h * 0.4f)
            cubicTo(w * 0.8f, h * 0.3f, w * 0.9f, h * 0.2f, w, h * 0.25f)
        }
        drawPath(river, Color(0xFF6FB1E0).copy(alpha = 0.5f), style = Stroke(width = 14.dp.toPx()))
        drawCircle(Color(0xFF7DBB7A).copy(alpha = 0.4f), radius = h * 0.16f, center = Offset(w * 0.18f, h * 0.75f))
        drawCircle(Color(0xFF7DBB7A).copy(alpha = 0.4f), radius = h * 0.12f, center = Offset(w * 0.82f, h * 0.62f))
    }
}

@Composable
private fun StationList(
    catalog: CatalogState,
    modifier: Modifier,
    onSeeStations: () -> Unit,
    onDirections: (EstacionResponseDto) -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        catalog.estaciones.take(4).forEach { estacion ->
            val available = catalog.availableAt(estacion.id)
            AppCard(modifier = Modifier.fillMaxWidth(), lift = true) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(Modifier.size(10.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(estacion.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        StatusBadge(
                            text = if (available > 0) "$available disponibles" else "Sin disponibilidad",
                            tone = if (available > 0) BadgeTone.SUCCESS else BadgeTone.NEUTRAL,
                        )
                    }
                    TextButton(onClick = { onDirections(estacion) }) { Text("Cómo llegar") }
                }
            }
        }
        OutlinedButton(onClick = onSeeStations, modifier = Modifier.fillMaxWidth()) { Text("Ver todas las estaciones") }
    }
}

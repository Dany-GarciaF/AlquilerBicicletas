package com.alquilerbicicletas.team_bici_frontend.ui.estaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogState
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
import com.alquilerbicicletas.team_bici_frontend.ui.components.EmptyState
import com.alquilerbicicletas.team_bici_frontend.ui.components.ErrorState
import com.alquilerbicicletas.team_bici_frontend.ui.components.MapPin
import com.alquilerbicicletas.team_bici_frontend.ui.maps.MapLinks
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageGrid
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageHeader
import com.alquilerbicicletas.team_bici_frontend.ui.components.StatusBadge
import com.alquilerbicicletas.team_bici_frontend.ui.components.fullSpan
import com.alquilerbicicletas.team_bici_frontend.ui.components.skeletonItems
import kotlinx.coroutines.launch

@Composable
fun EstacionesScreen(
    catalog: CatalogState,
    onViewBikes: (EstacionResponseDto) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    PageGrid(
        minCardWidth = 320.dp,
        header = {
            PageHeader(
                title = "Estaciones",
                subtitle = "Encuentra dónde retirar y devolver tu bicicleta. Toca el marcador o el botón para abrirla en Google Maps.",
            )
        },
    ) {
        when {
            catalog.showSkeleton -> skeletonItems(count = 4, height = 220.dp)
            catalog.loadFailed -> fullSpan("load-error") {
                ErrorState(message = catalog.error.orEmpty(), onRetry = { scope.launch { catalog.load() } })
            }
            catalog.estaciones.isEmpty() -> fullSpan("empty") {
                EmptyState(
                    title = "Aún no hay estaciones",
                    message = "Cuando se registren estaciones activas aparecerán aquí.",
                    actionLabel = "Actualizar",
                    onAction = { scope.launch { catalog.load() } },
                )
            }
            else -> items(catalog.estaciones, key = { it.id }) { estacion ->
                EstacionCard(
                    estacion = estacion,
                    available = catalog.availableAt(estacion.id),
                    onOpenMap = { uriHandler.openUri(MapLinks.place(estacion)) },
                    onViewBikes = { onViewBikes(estacion) },
                )
            }
        }
    }
}

@Composable
private fun EstacionCard(
    estacion: EstacionResponseDto,
    available: Int,
    onOpenMap: () -> Unit,
    onViewBikes: () -> Unit,
) {
    AppCard(lift = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer),
        ) {
            MapPin(
                color = MaterialTheme.colorScheme.primary,
                onClick = onOpenMap,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp).size(40.dp),
            )
            Text(
                text = estacion.codigo,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 72.dp),
            )
            StatusBadge(
                text = if (available > 0) "$available disponibles" else "Sin disponibilidad",
                tone = if (available > 0) BadgeTone.SUCCESS else BadgeTone.NEUTRAL,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            )
        }
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(estacion.nombre, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                estacion.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(onClick = onOpenMap, modifier = Modifier.weight(1f)) { Text("Ver en Google Maps") }
                Button(onClick = onViewBikes, modifier = Modifier.weight(1f)) { Text("Ver bicicletas") }
            }
        }
    }
}

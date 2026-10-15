package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.ui.bicicletas.BicicletaCard
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.BikeActions
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogState
import com.alquilerbicicletas.team_bici_frontend.ui.components.EmptyState
import com.alquilerbicicletas.team_bici_frontend.ui.components.ErrorState
import com.alquilerbicicletas.team_bici_frontend.ui.components.SkeletonCard
import kotlinx.coroutines.launch

@Composable
fun FeaturedBikesSection(
    catalog: CatalogState,
    actions: BikeActions,
    onSeeAll: () -> Unit,
) {
    val columns = responsiveColumns()
    val scope = rememberCoroutineScope()
    SectionContainer(verticalPadding = 48.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
            SectionTitle(
                title = "Bicicletas disponibles",
                subtitle = "Elige una y reserva o alquila. Te pediremos iniciar sesión para confirmar.",
                modifier = Modifier.weight(1f),
            )
            if (columns > 1) TextButton(onClick = onSeeAll) { Text("Ver todas") }
        }
        Spacer(Modifier.height(24.dp))
        when {
            catalog.showSkeleton -> ResponsiveRows(items = List(columns) { it }, columns = columns) { SkeletonCard() }
            catalog.loadFailed -> ErrorState(
                message = catalog.error.orEmpty(),
                onRetry = { scope.launch { catalog.load() } },
            )
            catalog.bicicletas.isEmpty() -> EmptyState(
                title = "Sin bicicletas disponibles",
                message = "Vuelve en un momento: las bicicletas devueltas aparecen aquí automáticamente.",
                actionLabel = "Actualizar",
                onAction = { scope.launch { catalog.load() } },
            )
            else -> ResponsiveRows(items = catalog.bicicletas.take(columns * 2), columns = columns) { bicicleta ->
                BicicletaCard(
                    bicicleta = bicicleta,
                    tarifa = catalog.tarifaFor(bicicleta.tipo),
                    enabled = !actions.submitting,
                    onReserve = { actions.reserve(bicicleta) },
                    onRent = { actions.rent(bicicleta) },
                )
            }
        }
        if (columns == 1) {
            TextButton(onClick = onSeeAll, modifier = Modifier.padding(top = 12.dp)) { Text("Ver todas las bicicletas") }
        }
    }
}

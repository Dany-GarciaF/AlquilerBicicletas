package com.alquilerbicicletas.team_bici_frontend.ui.bicicletas

import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.BikeActions
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogFilters
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogState
import com.alquilerbicicletas.team_bici_frontend.ui.components.EmptyState
import com.alquilerbicicletas.team_bici_frontend.ui.components.ErrorState
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageGrid
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageHeader
import com.alquilerbicicletas.team_bici_frontend.ui.components.fullSpan
import com.alquilerbicicletas.team_bici_frontend.ui.components.skeletonItems
import kotlinx.coroutines.launch

@Composable
fun BicicletasScreen(
    catalog: CatalogState,
    filters: CatalogFilters,
    actions: BikeActions,
) {
    val scope = rememberCoroutineScope()
    val visibles = filters.apply(catalog.bicicletas)
    val hasFilters = filters.estacionId != null || filters.tipo != null

    PageGrid(
        minCardWidth = 300.dp,
        header = {
            PageHeader(
                title = "Bicicletas disponibles",
                subtitle = "Filtra por estación o tipo, y reserva o inicia un alquiler. Necesitas una cuenta para confirmar.",
            )
        },
    ) {
        fullSpan("station-filter") {
            StationFilter(
                estaciones = catalog.estaciones,
                selectedId = filters.estacionId,
                enabled = !catalog.showSkeleton,
                onSelected = { filters.estacionId = it },
            )
        }
        fullSpan("tipo-filter") {
            TipoFilter(selected = filters.tipo, onSelected = { filters.tipo = it })
        }

        when {
            catalog.showSkeleton -> skeletonItems()
            catalog.loadFailed -> fullSpan("load-error") {
                ErrorState(message = catalog.error.orEmpty(), onRetry = { scope.launch { catalog.load() } })
            }
            visibles.isEmpty() -> fullSpan("empty") {
                EmptyState(
                    title = "Sin bicicletas disponibles",
                    message = if (hasFilters) {
                        "No hay bicicletas con estos filtros por ahora. Prueba con otra estación o tipo."
                    } else {
                        "No hay bicicletas disponibles por ahora."
                    },
                    actionLabel = if (hasFilters) "Quitar filtros" else "Actualizar",
                    onAction = {
                        if (hasFilters) {
                            filters.estacionId = null
                            filters.tipo = null
                        } else {
                            scope.launch { catalog.load() }
                        }
                    },
                )
            }
            else -> items(visibles, key = { it.id }) { bicicleta ->
                BicicletaCard(
                    bicicleta = bicicleta,
                    tarifa = catalog.tarifaFor(bicicleta.tipo),
                    enabled = !actions.submitting,
                    onReserve = { actions.reserve(bicicleta) },
                    onRent = { actions.rent(bicicleta) },
                )
            }
        }
    }
}

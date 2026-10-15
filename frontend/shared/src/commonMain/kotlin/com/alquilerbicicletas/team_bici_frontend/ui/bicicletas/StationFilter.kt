package com.alquilerbicicletas.team_bici_frontend.ui.bicicletas

import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import com.alquilerbicicletas.team_bici_frontend.ui.components.ChipRow

@Composable
fun StationFilter(
    estaciones: List<EstacionResponseDto>,
    selectedId: Long?,
    enabled: Boolean,
    onSelected: (Long?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Filtrar por estación", style = MaterialTheme.typography.titleMedium)
        ChipRow {
            FilterChip(
                selected = selectedId == null,
                onClick = { onSelected(null) },
                label = { Text("Todas") },
                enabled = enabled,
            )
            estaciones.forEach { estacion ->
                FilterChip(
                    selected = selectedId == estacion.id,
                    onClick = { onSelected(estacion.id) },
                    label = { Text(estacion.nombre) },
                    enabled = enabled,
                )
            }
        }
    }
}

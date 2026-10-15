package com.alquilerbicicletas.team_bici_frontend.ui.bicicletas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta
import com.alquilerbicicletas.team_bici_frontend.ui.components.ChipRow
import com.alquilerbicicletas.team_bici_frontend.ui.tipoLabel

@Composable
fun TipoFilter(
    selected: TipoBicicleta?,
    onSelected: (TipoBicicleta?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Filtrar por tipo", style = MaterialTheme.typography.titleMedium)
        ChipRow {
            FilterChip(selected = selected == null, onClick = { onSelected(null) }, label = { Text("Todos") })
            TipoBicicleta.entries.forEach { tipo ->
                FilterChip(
                    selected = selected == tipo,
                    onClick = { onSelected(tipo) },
                    label = { Text(tipoLabel(tipo)) },
                )
            }
        }
    }
}

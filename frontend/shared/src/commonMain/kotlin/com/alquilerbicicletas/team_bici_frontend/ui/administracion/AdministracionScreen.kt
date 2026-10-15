package com.alquilerbicicletas.team_bici_frontend.ui.administracion

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.network.BicicletaRepository
import com.alquilerbicicletas.team_bici_frontend.network.EstacionRepository
import com.alquilerbicicletas.team_bici_frontend.network.TarifaRepository
import com.alquilerbicicletas.team_bici_frontend.ui.components.ChipRow
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageHeader
import com.alquilerbicicletas.team_bici_frontend.ui.estadoLabel

/** Secciones del panel de administración. */
private enum class AdminSection { ESTACIONES, TARIFAS, BICICLETAS }

@Composable
fun AdministracionScreen(
    estacionRepository: EstacionRepository,
    tarifaRepository: TarifaRepository,
    bicicletaRepository: BicicletaRepository,
) {
    var section by remember { mutableStateOf(AdminSection.ESTACIONES) }
    val header: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PageHeader("Administración", "Configura la red de estaciones, las tarifas y la flota operativa.")
            ChipRow {
                AdminSection.entries.forEach {
                    FilterChip(
                        selected = section == it,
                        onClick = { section = it },
                        label = { Text(estadoLabel(it.name)) },
                    )
                }
            }
        }
    }
    AnimatedContent(targetState = section, label = "admin-section") { activeSection ->
        when (activeSection) {
            AdminSection.ESTACIONES -> EstacionesAdmin(estacionRepository, header)
            AdminSection.TARIFAS -> TarifasAdmin(tarifaRepository, header)
            AdminSection.BICICLETAS -> BicicletasAdmin(bicicletaRepository, estacionRepository, header)
        }
    }
}

package com.alquilerbicicletas.team_bici_frontend.ui.administracion

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.TarifaRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.TarifaRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.TarifaResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
import com.alquilerbicicletas.team_bici_frontend.ui.components.ChipRow
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormDialog
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormTextField
import com.alquilerbicicletas.team_bici_frontend.ui.components.StatusBadge
import com.alquilerbicicletas.team_bici_frontend.ui.estadoLabel
import com.alquilerbicicletas.team_bici_frontend.ui.formatCurrency
import kotlinx.coroutines.launch

@Composable
fun TarifasAdmin(repository: TarifaRepository, header: @Composable () -> Unit) {
    var entries by remember { mutableStateOf<List<TarifaResponseDto>>(emptyList()) }
    var editing by remember { mutableStateOf<TarifaResponseDto?>(null) }
    var creating by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    suspend fun reload() {
        loading = true
        error = null
        try {
            entries = repository.list()
        } catch (exception: ApiRequestException) {
            error = exception.message
        } finally {
            loading = false
        }
    }
    LaunchedEffect(Unit) { reload() }

    if (creating || editing != null) {
        TarifaForm(editing, { creating = false; editing = null }) { request ->
            scope.launch {
                try {
                    if (editing == null) repository.create(request) else repository.update(editing!!.id, request)
                    creating = false
                    editing = null
                    reload()
                } catch (exception: ApiRequestException) {
                    error = exception.message
                }
            }
        }
    }

    AdminSectionLayout(
        header = header,
        title = "Tarifas",
        actionLabel = "Nueva tarifa",
        loading = loading,
        error = error,
        isEmpty = entries.isEmpty(),
        emptyMessage = "Define una tarifa por tipo de bicicleta para poder alquilarlas.",
        onCreate = { creating = true },
    ) {
        items(entries, key = { it.id }) { entry ->
            AppCard(Modifier.fillMaxWidth().animateContentSize()) {
                AdminCardBody {
                    Text(estadoLabel(entry.tipoBicicleta.name), style = MaterialTheme.typography.titleLarge)
                    Text("${formatCurrency(entry.valorHora)} por hora", style = MaterialTheme.typography.titleMedium)
                    StatusBadge(
                        text = if (entry.activa) "Tarifa activa" else "Tarifa inactiva",
                        tone = if (entry.activa) BadgeTone.SUCCESS else BadgeTone.DANGER,
                    )
                    OutlinedButton(onClick = { editing = entry }) { Text("Editar tarifa") }
                }
            }
        }
    }
}

@Composable
private fun TarifaForm(initial: TarifaResponseDto?, onDismiss: () -> Unit, onSave: (TarifaRequestDto) -> Unit) {
    var type by remember(initial) { mutableStateOf(initial?.tipoBicicleta ?: TipoBicicleta.URBANA) }
    var value by remember(initial) { mutableStateOf(initial?.valorHora?.toString().orEmpty()) }
    var active by remember(initial) { mutableStateOf(initial?.activa ?: true) }
    var valueError by remember(initial) { mutableStateOf<String?>(null) }

    FormDialog(
        title = if (initial == null) "Nueva tarifa" else "Editar tarifa",
        onDismiss = onDismiss,
        onConfirm = {
            val parsed = value.toDoubleOrNull()
            valueError = if (parsed == null) "Ingresa un valor numérico" else null
            parsed?.let { onSave(TarifaRequestDto(type, it, active)) }
        },
    ) {
        Text("Tipo de bicicleta")
        ChipRow {
            TipoBicicleta.entries.forEach {
                FilterChip(type == it, { type = it }, { Text(estadoLabel(it.name)) })
            }
        }
        FormTextField("Valor por hora", value, valueError) { value = it; valueError = null }
        FilterChip(active, { active = !active }, { Text(if (active) "Tarifa activa" else "Tarifa inactiva") })
    }
}

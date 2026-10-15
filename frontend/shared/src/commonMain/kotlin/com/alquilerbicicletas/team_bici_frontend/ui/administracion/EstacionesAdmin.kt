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
import com.alquilerbicicletas.team_bici_frontend.network.EstacionRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormDialog
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormTextField
import com.alquilerbicicletas.team_bici_frontend.ui.components.StatusBadge
import kotlinx.coroutines.launch

@Composable
fun EstacionesAdmin(repository: EstacionRepository, header: @Composable () -> Unit) {
    var entries by remember { mutableStateOf<List<EstacionResponseDto>>(emptyList()) }
    var editing by remember { mutableStateOf<EstacionResponseDto?>(null) }
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
        EstacionForm(
            initial = editing,
            onDismiss = { creating = false; editing = null },
            onSave = { request ->
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
            },
        )
    }

    AdminSectionLayout(
        header = header,
        title = "Estaciones",
        actionLabel = "Nueva estación",
        loading = loading,
        error = error,
        isEmpty = entries.isEmpty(),
        emptyMessage = "Crea la primera estación para empezar a recibir bicicletas.",
        onCreate = { creating = true },
    ) {
        items(entries, key = { it.id }) { entry ->
            AppCard(Modifier.fillMaxWidth().animateContentSize()) {
                AdminCardBody {
                    Text(entry.nombre, style = MaterialTheme.typography.titleLarge)
                    Text("${entry.codigo} · ${entry.direccion}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    StatusBadge(
                        text = if (entry.activa) "Activa" else "Inactiva",
                        tone = if (entry.activa) BadgeTone.SUCCESS else BadgeTone.DANGER,
                    )
                    OutlinedButton(onClick = { editing = entry }) { Text("Editar estación") }
                }
            }
        }
    }
}

@Composable
private fun EstacionForm(initial: EstacionResponseDto?, onDismiss: () -> Unit, onSave: (EstacionRequestDto) -> Unit) {
    var codigo by remember(initial) { mutableStateOf(initial?.codigo.orEmpty()) }
    var nombre by remember(initial) { mutableStateOf(initial?.nombre.orEmpty()) }
    var direccion by remember(initial) { mutableStateOf(initial?.direccion.orEmpty()) }
    var latitud by remember(initial) { mutableStateOf(initial?.latitud?.toString().orEmpty()) }
    var longitud by remember(initial) { mutableStateOf(initial?.longitud?.toString().orEmpty()) }
    var activa by remember(initial) { mutableStateOf(initial?.activa ?: true) }
    var submitted by remember(initial) { mutableStateOf(false) }
    var latitudError by remember(initial) { mutableStateOf<String?>(null) }
    var longitudError by remember(initial) { mutableStateOf<String?>(null) }
    val required = "Campo obligatorio"

    FormDialog(
        title = if (initial == null) "Nueva estación" else "Editar estación",
        onDismiss = onDismiss,
        onConfirm = {
            submitted = true
            val lat = latitud.toDoubleOrNull()
            val lon = longitud.toDoubleOrNull()
            latitudError = if (lat == null || lat !in -90.0..90.0) {
                "La latitud debe estar entre -90 y 90"
            } else {
                null
            }
            longitudError = if (lon == null || lon !in -180.0..180.0) {
                "La longitud debe estar entre -180 y 180"
            } else {
                null
            }
            if (
                codigo.isNotBlank() &&
                nombre.isNotBlank() &&
                direccion.isNotBlank() &&
                latitudError == null &&
                longitudError == null
            ) {
                onSave(EstacionRequestDto(codigo.trim(), nombre.trim(), direccion.trim(), lat!!, lon!!, activa))
            }
        },
    ) {
        Text("Datos de ubicación", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        FormTextField("Código", codigo, required.takeIf { submitted && codigo.isBlank() }) { codigo = it }
        FormTextField("Nombre", nombre, required.takeIf { submitted && nombre.isBlank() }) { nombre = it }
        FormTextField("Dirección", direccion, required.takeIf { submitted && direccion.isBlank() }) { direccion = it }
        FormTextField("Latitud", latitud, latitudError) { latitud = it; latitudError = null }
        FormTextField("Longitud", longitud, longitudError) { longitud = it; longitudError = null }
        Text(
            "Ejemplo Bogotá: 4.6097, -74.0817",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FilterChip(activa, { activa = !activa }, { Text(if (activa) "Estación activa" else "Estación inactiva") })
    }
}

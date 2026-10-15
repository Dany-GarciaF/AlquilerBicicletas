package com.alquilerbicicletas.team_bici_frontend.ui.alquileres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.network.AlquilerRepository
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.EstacionRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.AlquilerResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.DevolucionRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstadoAlquiler
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
import com.alquilerbicicletas.team_bici_frontend.ui.components.ChipRow
import com.alquilerbicicletas.team_bici_frontend.ui.components.EmptyState
import com.alquilerbicicletas.team_bici_frontend.ui.components.ErrorState
import com.alquilerbicicletas.team_bici_frontend.ui.components.FeedbackBanner
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormDialog
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageGrid
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageHeader
import com.alquilerbicicletas.team_bici_frontend.ui.components.StatusBadge
import com.alquilerbicicletas.team_bici_frontend.ui.components.fullSpan
import com.alquilerbicicletas.team_bici_frontend.ui.components.skeletonItems
import com.alquilerbicicletas.team_bici_frontend.ui.estadoLabel
import com.alquilerbicicletas.team_bici_frontend.ui.formatCurrency
import com.alquilerbicicletas.team_bici_frontend.ui.formatDateTime
import kotlinx.coroutines.launch

@Composable
fun MisAlquileresScreen(
    alquilerRepository: AlquilerRepository,
    estacionRepository: EstacionRepository,
) {
    var alquileres by remember { mutableStateOf<List<AlquilerResponseDto>>(emptyList()) }
    var estaciones by remember { mutableStateOf<List<EstacionResponseDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var loadFailed by remember { mutableStateOf(false) }
    var returning by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var alquilerParaDevolver by remember { mutableStateOf<AlquilerResponseDto?>(null) }
    val scope = rememberCoroutineScope()

    suspend fun reload() {
        loading = true
        loadFailed = false
        error = null
        try {
            if (estaciones.isEmpty()) {
                estaciones = estacionRepository.list().filter { it.activa }
            }
            alquileres = alquilerRepository.listMine()
        } catch (exception: ApiRequestException) {
            loadFailed = true
            error = exception.message
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) { reload() }

    alquilerParaDevolver?.let { alquiler ->
        DevolucionDialog(
            estaciones = estaciones,
            submitting = returning,
            onDismiss = { if (!returning) alquilerParaDevolver = null },
            onConfirm = { estacionId ->
                scope.launch {
                    returning = true
                    error = null
                    try {
                        alquilerRepository.returnBike(alquiler.id, DevolucionRequestDto(estacionId))
                        alquilerParaDevolver = null
                        success = "La bicicleta fue devuelta correctamente"
                        reload()
                    } catch (exception: ApiRequestException) {
                        error = exception.message
                    } finally {
                        returning = false
                    }
                }
            },
        )
    }

    val showLoadError = loadFailed && !loading && alquileres.isEmpty()

    PageGrid(
        minCardWidth = 340.dp,
        header = { PageHeader("Mis alquileres", "Revisa tus alquileres y devuelve las bicicletas activas.") },
    ) {
        if (success != null) fullSpan("success") { FeedbackBanner(success) }
        if (error != null && !showLoadError) fullSpan("error") { FeedbackBanner(error, isError = true) }

        when {
            loading -> skeletonItems(count = 4, height = 200.dp)
            showLoadError -> fullSpan("load-error") {
                ErrorState(message = error.orEmpty(), onRetry = { scope.launch { reload() } })
            }
            alquileres.isEmpty() -> fullSpan("empty") {
                EmptyState(
                    title = "Aún no tienes alquileres",
                    message = "Cuando alquiles una bicicleta podrás seguirla y devolverla desde aquí.",
                    actionLabel = "Actualizar",
                    onAction = { scope.launch { reload() } },
                )
            }
            else -> items(alquileres, key = { it.id }) { alquiler ->
                AlquilerCard(
                    alquiler = alquiler,
                    enabled = !returning,
                    onReturn = { alquilerParaDevolver = alquiler },
                )
            }
        }
    }
}

@Composable
private fun AlquilerCard(
    alquiler: AlquilerResponseDto,
    enabled: Boolean,
    onReturn: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Alquiler #${alquiler.id}", style = MaterialTheme.typography.titleLarge)
                StatusBadge(estadoLabel(alquiler.estado.name), alquiler.estado.tone())
            }
            Text(
                "Bicicleta #${alquiler.bicicletaId} · Retiro: estación #${alquiler.estacionRetiroId}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("Inicio: ${formatDateTime(alquiler.fechaInicio)}")
            alquiler.fechaFin?.let { Text("Fin: ${formatDateTime(it)}") }
            alquiler.costoFinal?.let {
                Text("Costo final: ${formatCurrency(it)}", style = MaterialTheme.typography.titleMedium)
            }
            if (alquiler.estado == EstadoAlquiler.ACTIVO) {
                Button(onClick = onReturn, enabled = enabled) {
                    Text("Devolver bicicleta")
                }
            }
        }
    }
}

@Composable
private fun DevolucionDialog(
    estaciones: List<EstacionResponseDto>,
    submitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    var estacionId by remember { mutableStateOf<Long?>(null) }

    FormDialog(
        title = "Devolver bicicleta",
        onDismiss = onDismiss,
        onConfirm = { estacionId?.let(onConfirm) },
        confirmLabel = if (submitting) "Devolviendo..." else "Confirmar devolución",
        confirmEnabled = estacionId != null && !submitting,
        dismissEnabled = !submitting,
    ) {
        Text("Selecciona la estación donde entregas la bicicleta.")
        ChipRow {
            estaciones.forEach { estacion ->
                FilterChip(
                    selected = estacionId == estacion.id,
                    onClick = { estacionId = estacion.id },
                    enabled = !submitting,
                    label = { Text(estacion.nombre) },
                )
            }
        }
        if (estaciones.isEmpty()) {
            Text("No hay estaciones activas disponibles.", color = MaterialTheme.colorScheme.error)
        }
    }
}

private fun EstadoAlquiler.tone(): BadgeTone = when (this) {
    EstadoAlquiler.ACTIVO -> BadgeTone.ACCENT
    EstadoAlquiler.FINALIZADO -> BadgeTone.SUCCESS
    EstadoAlquiler.CANCELADO -> BadgeTone.DANGER
}

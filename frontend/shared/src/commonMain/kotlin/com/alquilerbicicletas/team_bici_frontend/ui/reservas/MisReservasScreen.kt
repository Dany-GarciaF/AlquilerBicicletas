package com.alquilerbicicletas.team_bici_frontend.ui.reservas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.ReservaRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstadoReserva
import com.alquilerbicicletas.team_bici_frontend.network.dto.ReservaResponseDto
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
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
import com.alquilerbicicletas.team_bici_frontend.ui.formatDateTime
import kotlinx.coroutines.launch

@Composable
fun MisReservasScreen(reservaRepository: ReservaRepository) {
    var reservas by remember { mutableStateOf<List<ReservaResponseDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var loadFailed by remember { mutableStateOf(false) }
    var cancelling by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var reservaParaCancelar by remember { mutableStateOf<ReservaResponseDto?>(null) }
    val scope = rememberCoroutineScope()

    suspend fun reload() {
        loading = true
        loadFailed = false
        error = null
        try {
            reservas = reservaRepository.listMine()
        } catch (exception: ApiRequestException) {
            loadFailed = true
            error = exception.message
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) { reload() }

    reservaParaCancelar?.let { reserva ->
        FormDialog(
            title = "Cancelar reserva",
            onDismiss = { if (!cancelling) reservaParaCancelar = null },
            confirmLabel = if (cancelling) "Cancelando..." else "Confirmar cancelación",
            confirmEnabled = !cancelling,
            dismissLabel = "Volver",
            dismissEnabled = !cancelling,
            onConfirm = {
                scope.launch {
                    cancelling = true
                    error = null
                    try {
                        reservaRepository.cancel(reserva.id)
                        reservaParaCancelar = null
                        success = "La reserva fue cancelada"
                        reload()
                    } catch (exception: ApiRequestException) {
                        error = exception.message
                    } finally {
                        cancelling = false
                    }
                }
            },
        ) {
            Text("¿Deseas cancelar la reserva #${reserva.id}? Esta operación no se puede deshacer.")
        }
    }

    val showLoadError = loadFailed && !loading && reservas.isEmpty()

    PageGrid(
        minCardWidth = 340.dp,
        header = { PageHeader("Mis reservas", "Consulta y administra tus próximas reservas.") },
    ) {
        if (success != null) fullSpan("success") { FeedbackBanner(success) }
        if (error != null && !showLoadError) fullSpan("error") { FeedbackBanner(error, isError = true) }

        when {
            loading -> skeletonItems(count = 4, height = 190.dp)
            showLoadError -> fullSpan("load-error") {
                ErrorState(message = error.orEmpty(), onRetry = { scope.launch { reload() } })
            }
            reservas.isEmpty() -> fullSpan("empty") {
                EmptyState(
                    title = "Aún no tienes reservas",
                    message = "Cuando reserves una bicicleta aparecerá aquí.",
                    actionLabel = "Actualizar",
                    onAction = { scope.launch { reload() } },
                )
            }
            else -> items(reservas, key = { it.id }) { reserva ->
                ReservaCard(
                    reserva = reserva,
                    enabled = !cancelling,
                    onCancel = { reservaParaCancelar = reserva },
                )
            }
        }
    }
}

@Composable
private fun ReservaCard(
    reserva: ReservaResponseDto,
    enabled: Boolean,
    onCancel: () -> Unit,
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
                Text("Reserva #${reserva.id}", style = MaterialTheme.typography.titleLarge)
                StatusBadge(estadoLabel(reserva.estado.name), reserva.estado.tone())
            }
            Text(
                "Bicicleta #${reserva.bicicletaId} · Estación #${reserva.estacionRetiroId}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("Inicio: ${formatDateTime(reserva.fechaInicio)}")
            Text("Fin: ${formatDateTime(reserva.fechaFin)}")
            if (reserva.estado == EstadoReserva.CONFIRMADA) {
                OutlinedButton(onClick = onCancel, enabled = enabled) {
                    Text("Cancelar reserva")
                }
            }
        }
    }
}

private fun EstadoReserva.tone(): BadgeTone = when (this) {
    EstadoReserva.PENDIENTE -> BadgeTone.WARNING
    EstadoReserva.CONFIRMADA -> BadgeTone.SUCCESS
    EstadoReserva.CANCELADA -> BadgeTone.DANGER
    EstadoReserva.COMPLETADA -> BadgeTone.NEUTRAL
}

package com.alquilerbicicletas.team_bici_frontend.ui.bicicletas

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaResponseDto
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormDialog
import com.alquilerbicicletas.team_bici_frontend.ui.localDateTimeToUtcIso
import com.alquilerbicicletas.team_bici_frontend.ui.ReservationDateTimePicker

@Composable
fun ReservaDialog(
    bicicleta: BicicletaResponseDto,
    submitting: Boolean,
    submitError: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var inicio by remember(bicicleta.id) { mutableStateOf("") }
    var fin by remember(bicicleta.id) { mutableStateOf("") }
    var error by remember(bicicleta.id) { mutableStateOf<String?>(null) }

    FormDialog(
        title = "Reservar ${bicicleta.codigo}",
        onDismiss = onDismiss,
        confirmLabel = if (submitting) "Confirmando..." else "Confirmar reserva",
        confirmEnabled = !submitting,
        dismissEnabled = !submitting,
        onConfirm = {
            error = when {
                inicio.isBlank() -> "La fecha de inicio es obligatoria"
                fin.isBlank() -> "La fecha de fin es obligatoria"
                !isValidReservationDateTime(inicio) || !isValidReservationDateTime(fin) ->
                    "Completa ambas horas en formato HH:mm."
                fin <= inicio -> "La finalización debe ser posterior al inicio."
                else -> null
            }

            if (error == null) {
                onConfirm(
                    localDateTimeToUtcIso(inicio.substringBefore('T'), inicio.substringAfter('T').take(5)),
                    localDateTimeToUtcIso(fin.substringBefore('T'), fin.substringAfter('T').take(5)),
                )
            }
        },
    ) {
        Text("Estación de retiro: ${bicicleta.estacionNombre}")
        Text("Selecciona el día y luego indica la hora de retiro y devolución.")
        ReservationDateTimePicker(
            label = "Inicio de la reserva",
            value = inicio,
            enabled = !submitting,
            onValueChange = { inicio = it },
        )
        ReservationDateTimePicker(
            label = "Fin de la reserva",
            value = fin,
            enabled = !submitting,
            onValueChange = { fin = it },
        )
        (error ?: submitError)?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

private fun isValidReservationDateTime(value: String): Boolean =
    Regex("""\d{4}-\d{2}-\d{2}T([01]\d|2[0-3]):[0-5]\d:00""").matches(value)

@Composable
fun AlquilerDialog(
    bicicleta: BicicletaResponseDto,
    submitting: Boolean,
    submitError: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    FormDialog(
        title = "Iniciar alquiler",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmLabel = if (submitting) "Iniciando..." else "Confirmar alquiler",
        confirmEnabled = !submitting,
        dismissEnabled = !submitting,
    ) {
        Text("Vas a retirar ${bicicleta.codigo} en ${bicicleta.estacionNombre}. El costo se calculará al devolverla.")
        submitError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

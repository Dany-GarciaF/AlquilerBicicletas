package com.alquilerbicicletas.team_bici_frontend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDateTimePicker(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
) {
    var showCalendar by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val date = value.substringBefore('T').takeIf { it.length == 10 }.orEmpty()
    val time = value.substringAfter('T', "").take(5)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = if (date.isBlank()) "" else "${formatDate(date)} ${if (time.isBlank()) "" else "· $time"}",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { showCalendar = true }, enabled = enabled) {
                    Text("Elegir")
                }
            },
        )
        if (date.isNotBlank()) {
            OutlinedTextField(
                value = time.ifBlank { "09:00" },
                onValueChange = {},
                label = { Text("Hora de la reserva") },
                readOnly = true,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { showTimePicker = true }, enabled = enabled) {
                        Text("Elegir hora")
                    }
                },
            )
        }

        if (showTimePicker) {
            val initialHour = time.substringBefore(':').toIntOrNull() ?: 9
            val initialMinute = time.substringAfter(':', "").toIntOrNull() ?: 0
            val state = rememberTimePickerState(
                initialHour = initialHour,
                initialMinute = initialMinute,
                is24Hour = true,
            )
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Selecciona la hora") },
                text = { TimePicker(state = state) },
                confirmButton = {
                    Button(
                        onClick = {
                            onValueChange(
                                "${date}T${state.hour.toString().padStart(2, '0')}:${state.minute.toString().padStart(2, '0')}:00",
                            )
                            showTimePicker = false
                        },
                    ) { Text("Aplicar hora") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
                },
            )
        }
    }

    if (showCalendar) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                Button(
                    onClick = {
                        state.selectedDateMillis?.let {
                            onValueChange(
                                "${isoDateFromUtcMillis(it)}T${time.ifBlank { "09:00" }}:00",
                            )
                        }
                        showCalendar = false
                    },
                    enabled = state.selectedDateMillis != null,
                ) { Text("Aplicar fecha") }
            },
            dismissButton = {
                TextButton(onClick = { showCalendar = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

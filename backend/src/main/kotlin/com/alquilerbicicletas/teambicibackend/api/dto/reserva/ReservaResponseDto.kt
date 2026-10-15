package com.alquilerbicicletas.teambicibackend.api.dto.reserva

import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoReserva
import java.time.Instant

data class ReservaResponseDto(
    val id: Long,
    val usuarioId: Long,
    val bicicletaId: Long,
    val estacionRetiroId: Long,
    val fechaInicio: Instant,
    val fechaFin: Instant,
    val estado: EstadoReserva,
)

package com.alquilerbicicletas.teambicibackend.api.dto.alquiler

import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoAlquiler
import java.math.BigDecimal
import java.time.Instant

data class AlquilerResponseDto(
    val id: Long,
    val usuarioId: Long,
    val bicicletaId: Long,
    val estacionRetiroId: Long,
    val estacionDevolucionId: Long?,
    val fechaInicio: Instant,
    val fechaFin: Instant?,
    val estado: EstadoAlquiler,
    val tarifaHoraAplicada: BigDecimal,
    val costoFinal: BigDecimal?,
)

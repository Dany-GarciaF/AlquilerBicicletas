package com.alquilerbicicletas.teambicibackend.api.dto.reserva

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class ReservaRequestDto(
    @field:NotNull
    val bicicletaId: Long,
    @field:NotNull
    val estacionRetiroId: Long,
    @field:NotNull
    @field:Future
    val fechaInicio: Instant,
    @field:NotNull
    @field:Future
    val fechaFin: Instant,
)

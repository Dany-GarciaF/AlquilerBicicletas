package com.alquilerbicicletas.teambicibackend.api.dto.alquiler

import jakarta.validation.constraints.NotNull

data class AlquilerRequestDto(
    @field:NotNull
    val bicicletaId: Long,
    @field:NotNull
    val estacionRetiroId: Long,
)

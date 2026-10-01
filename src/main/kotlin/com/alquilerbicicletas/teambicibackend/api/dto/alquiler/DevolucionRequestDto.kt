package com.alquilerbicicletas.teambicibackend.api.dto.alquiler

import jakarta.validation.constraints.NotNull

data class DevolucionRequestDto(
    @field:NotNull
    val estacionDevolucionId: Long,
)

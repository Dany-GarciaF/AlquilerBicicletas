package com.alquilerbicicletas.teambicibackend.api.dto.bicicleta

import com.alquilerbicicletas.teambicibackend.domain.enums.TipoBicicleta
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class BicicletaRequestDto(
    @field:NotBlank
    @field:Size(max = 30)
    val codigo: String,
    @field:NotBlank
    @field:Size(max = 80)
    val marca: String,
    @field:NotBlank
    @field:Size(max = 80)
    val modelo: String,
    @field:NotNull
    val tipo: TipoBicicleta,
    @field:NotNull
    val estacionId: Long,
)

package com.alquilerbicicletas.teambicibackend.api.dto.estacion

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class EstacionRequestDto(
    @field:NotBlank
    @field:Size(max = 30)
    val codigo: String,
    @field:NotBlank
    @field:Size(max = 120)
    val nombre: String,
    @field:NotBlank
    @field:Size(max = 255)
    val direccion: String,
    @field:NotNull
    @field:DecimalMin("-90.000000")
    @field:DecimalMax("90.000000")
    val latitud: BigDecimal,
    @field:NotNull
    @field:DecimalMin("-180.000000")
    @field:DecimalMax("180.000000")
    val longitud: BigDecimal,
    val activa: Boolean = true,
)

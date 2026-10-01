package com.alquilerbicicletas.teambicibackend.api.dto.tarifa

import com.alquilerbicicletas.teambicibackend.domain.enums.TipoBicicleta
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class TarifaRequestDto(
    @field:NotNull
    val tipoBicicleta: TipoBicicleta,
    @field:DecimalMin("0.01")
    val valorHora: BigDecimal,
    val activa: Boolean = true,
)

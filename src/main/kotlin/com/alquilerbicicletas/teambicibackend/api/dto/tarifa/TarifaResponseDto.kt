package com.alquilerbicicletas.teambicibackend.api.dto.tarifa

import com.alquilerbicicletas.teambicibackend.domain.enums.TipoBicicleta
import java.math.BigDecimal

data class TarifaResponseDto(
    val id: Long,
    val tipoBicicleta: TipoBicicleta,
    val valorHora: BigDecimal,
    val activa: Boolean,
)

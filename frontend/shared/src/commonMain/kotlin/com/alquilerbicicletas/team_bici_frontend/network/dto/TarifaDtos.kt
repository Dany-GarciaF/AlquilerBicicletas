package com.alquilerbicicletas.team_bici_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TarifaRequestDto(
    val tipoBicicleta: TipoBicicleta,
    val valorHora: Double,
    val activa: Boolean = true,
)

@Serializable
data class TarifaResponseDto(
    val id: Long,
    val tipoBicicleta: TipoBicicleta,
    val valorHora: Double,
    val activa: Boolean,
)

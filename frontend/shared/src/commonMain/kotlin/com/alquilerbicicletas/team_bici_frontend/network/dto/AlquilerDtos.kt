package com.alquilerbicicletas.team_bici_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlquilerRequestDto(
    val bicicletaId: Long,
    val estacionRetiroId: Long,
)

@Serializable
data class DevolucionRequestDto(
    val estacionDevolucionId: Long,
)

@Serializable
data class AlquilerResponseDto(
    val id: Long,
    val usuarioId: Long,
    val bicicletaId: Long,
    val estacionRetiroId: Long,
    val estacionDevolucionId: Long? = null,
    val fechaInicio: String,
    val fechaFin: String? = null,
    val estado: EstadoAlquiler,
    val tarifaHoraAplicada: Double,
    val costoFinal: Double? = null,
)

@Serializable
enum class EstadoAlquiler {
    ACTIVO,
    FINALIZADO,
    CANCELADO,
}

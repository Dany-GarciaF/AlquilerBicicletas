package com.alquilerbicicletas.team_bici_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservaRequestDto(
    val bicicletaId: Long,
    val estacionRetiroId: Long,
    val fechaInicio: String,
    val fechaFin: String,
)

@Serializable
data class ReservaResponseDto(
    val id: Long,
    val usuarioId: Long,
    val bicicletaId: Long,
    val estacionRetiroId: Long,
    val fechaInicio: String,
    val fechaFin: String,
    val estado: EstadoReserva,
)

@Serializable
enum class EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    COMPLETADA,
}

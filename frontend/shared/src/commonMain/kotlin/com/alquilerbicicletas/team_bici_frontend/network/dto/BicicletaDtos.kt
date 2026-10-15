package com.alquilerbicicletas.team_bici_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class BicicletaRequestDto(
    val codigo: String,
    val marca: String,
    val modelo: String,
    val tipo: TipoBicicleta,
    val estacionId: Long,
)

@Serializable
data class BicicletaResponseDto(
    val id: Long,
    val codigo: String,
    val marca: String,
    val modelo: String,
    val tipo: TipoBicicleta,
    val estado: EstadoBicicleta,
    val estacionId: Long,
    val estacionNombre: String,
    val imagenUrl: String? = null,
)

@Serializable
enum class TipoBicicleta {
    URBANA,
    MONTANA,
    ELECTRICA,
    PLEGABLE,
}

@Serializable
enum class EstadoBicicleta {
    DISPONIBLE,
    RESERVADA,
    ALQUILADA,
    MANTENIMIENTO,
}

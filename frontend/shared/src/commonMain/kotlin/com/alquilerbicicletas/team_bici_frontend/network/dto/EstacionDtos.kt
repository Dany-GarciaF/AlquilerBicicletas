package com.alquilerbicicletas.team_bici_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class EstacionRequestDto(
    val codigo: String,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val activa: Boolean = true,
)

@Serializable
data class EstacionResponseDto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val activa: Boolean,
)

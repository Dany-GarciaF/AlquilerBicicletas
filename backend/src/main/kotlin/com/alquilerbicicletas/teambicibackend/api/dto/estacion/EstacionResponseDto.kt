package com.alquilerbicicletas.teambicibackend.api.dto.estacion

import java.math.BigDecimal

data class EstacionResponseDto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val direccion: String,
    val latitud: BigDecimal,
    val longitud: BigDecimal,
    val activa: Boolean,
)

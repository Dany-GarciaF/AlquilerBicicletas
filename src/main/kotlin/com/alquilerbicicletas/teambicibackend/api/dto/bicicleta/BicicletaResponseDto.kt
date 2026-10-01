package com.alquilerbicicletas.teambicibackend.api.dto.bicicleta

import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta
import com.alquilerbicicletas.teambicibackend.domain.enums.TipoBicicleta

data class BicicletaResponseDto(
    val id: Long,
    val codigo: String,
    val marca: String,
    val modelo: String,
    val tipo: TipoBicicleta,
    val estado: EstadoBicicleta,
    val estacionId: Long,
    val estacionNombre: String,
)

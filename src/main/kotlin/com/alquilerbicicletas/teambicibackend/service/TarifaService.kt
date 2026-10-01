package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.api.dto.tarifa.TarifaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.tarifa.TarifaResponseDto

interface TarifaService {
    fun crear(dto: TarifaRequestDto): TarifaResponseDto
    fun listar(): List<TarifaResponseDto>
    fun actualizar(id: Long, dto: TarifaRequestDto): TarifaResponseDto
}

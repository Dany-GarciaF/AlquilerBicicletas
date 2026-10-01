package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.api.dto.estacion.EstacionRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.estacion.EstacionResponseDto

interface EstacionService {
    fun crear(dto: EstacionRequestDto): EstacionResponseDto
    fun listar(): List<EstacionResponseDto>
    fun obtener(id: Long): EstacionResponseDto
    fun actualizar(id: Long, dto: EstacionRequestDto): EstacionResponseDto
    fun eliminar(id: Long)
}

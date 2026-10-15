package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.AlquilerRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.AlquilerResponseDto
import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.DevolucionRequestDto

/** Casos de uso de alquileres: iniciar, listar los del usuario y devolver. */
interface AlquilerService {
    fun crear(usuarioId: Long, dto: AlquilerRequestDto): AlquilerResponseDto
    fun listarPorUsuario(usuarioId: Long): List<AlquilerResponseDto>
    fun devolver(id: Long, usuarioId: Long, dto: DevolucionRequestDto): AlquilerResponseDto
}

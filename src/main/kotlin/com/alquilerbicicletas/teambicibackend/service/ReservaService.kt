package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.api.dto.reserva.ReservaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.reserva.ReservaResponseDto

interface ReservaService {
    fun crear(usuarioId: Long, dto: ReservaRequestDto): ReservaResponseDto
    fun listarPorUsuario(usuarioId: Long): List<ReservaResponseDto>
    fun cancelar(id: Long, usuarioId: Long): ReservaResponseDto
}

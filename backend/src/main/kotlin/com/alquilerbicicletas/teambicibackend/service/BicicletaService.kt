package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.api.dto.bicicleta.BicicletaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.bicicleta.BicicletaResponseDto
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta

/** Casos de uso de bicicletas: gestión de la flota, estado, disponibilidad e imagen. */
interface BicicletaService {
    fun crear(dto: BicicletaRequestDto): BicicletaResponseDto
    fun listar(): List<BicicletaResponseDto>
    fun obtener(id: Long): BicicletaResponseDto
    fun actualizar(id: Long, dto: BicicletaRequestDto): BicicletaResponseDto
    fun cambiarEstado(id: Long, estado: EstadoBicicleta): BicicletaResponseDto
    fun disponibles(estacionId: Long?): List<BicicletaResponseDto>
    fun actualizarImagen(id: Long, content: ByteArray): BicicletaResponseDto
    fun eliminarImagen(id: Long): BicicletaResponseDto
}

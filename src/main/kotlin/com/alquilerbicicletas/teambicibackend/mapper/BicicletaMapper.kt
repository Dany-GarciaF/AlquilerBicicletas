package com.alquilerbicicletas.teambicibackend.mapper

import com.alquilerbicicletas.teambicibackend.api.dto.bicicleta.BicicletaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.bicicleta.BicicletaResponseDto
import com.alquilerbicicletas.teambicibackend.domain.entity.Bicicleta
import com.alquilerbicicletas.teambicibackend.domain.entity.Estacion
import org.springframework.stereotype.Component

@Component
class BicicletaMapper {
    fun toEntity(dto: BicicletaRequestDto, estacion: Estacion): Bicicleta =
        Bicicleta(
            codigo = dto.codigo.trim().uppercase(),
            marca = dto.marca.trim(),
            modelo = dto.modelo.trim(),
            tipo = dto.tipo,
            estacionActual = estacion,
        )

    fun updateEntity(dto: BicicletaRequestDto, entity: Bicicleta, estacion: Estacion) {
        entity.codigo = dto.codigo.trim().uppercase()
        entity.marca = dto.marca.trim()
        entity.modelo = dto.modelo.trim()
        entity.tipo = dto.tipo
        entity.estacionActual = estacion
    }

    fun toResponse(entity: Bicicleta): BicicletaResponseDto =
        BicicletaResponseDto(
            id = entity.id!!,
            codigo = entity.codigo,
            marca = entity.marca,
            modelo = entity.modelo,
            tipo = entity.tipo,
            estado = entity.estado,
            estacionId = entity.estacionActual.id!!,
            estacionNombre = entity.estacionActual.nombre,
        )
}

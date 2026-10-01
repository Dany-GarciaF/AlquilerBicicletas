package com.alquilerbicicletas.teambicibackend.mapper

import com.alquilerbicicletas.teambicibackend.api.dto.estacion.EstacionRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.estacion.EstacionResponseDto
import com.alquilerbicicletas.teambicibackend.domain.entity.Estacion
import org.springframework.stereotype.Component

@Component
class EstacionMapper {
    fun toEntity(dto: EstacionRequestDto): Estacion =
        Estacion(
            codigo = dto.codigo.trim().uppercase(),
            nombre = dto.nombre.trim(),
            direccion = dto.direccion.trim(),
            latitud = dto.latitud,
            longitud = dto.longitud,
            activa = dto.activa,
        )

    fun updateEntity(dto: EstacionRequestDto, entity: Estacion) {
        entity.codigo = dto.codigo.trim().uppercase()
        entity.nombre = dto.nombre.trim()
        entity.direccion = dto.direccion.trim()
        entity.latitud = dto.latitud
        entity.longitud = dto.longitud
        entity.activa = dto.activa
    }

    fun toResponse(entity: Estacion): EstacionResponseDto =
        EstacionResponseDto(
            id = entity.id!!,
            codigo = entity.codigo,
            nombre = entity.nombre,
            direccion = entity.direccion,
            latitud = entity.latitud,
            longitud = entity.longitud,
            activa = entity.activa,
        )
}

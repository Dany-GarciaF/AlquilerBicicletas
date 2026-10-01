package com.alquilerbicicletas.teambicibackend.mapper

import com.alquilerbicicletas.teambicibackend.api.dto.tarifa.TarifaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.tarifa.TarifaResponseDto
import com.alquilerbicicletas.teambicibackend.domain.entity.Tarifa
import org.springframework.stereotype.Component

@Component
class TarifaMapper {
    fun toEntity(dto: TarifaRequestDto): Tarifa =
        Tarifa(
            tipoBicicleta = dto.tipoBicicleta,
            valorHora = dto.valorHora,
            activa = dto.activa,
        )

    fun updateEntity(dto: TarifaRequestDto, entity: Tarifa) {
        entity.tipoBicicleta = dto.tipoBicicleta
        entity.valorHora = dto.valorHora
        entity.activa = dto.activa
    }

    fun toResponse(entity: Tarifa): TarifaResponseDto =
        TarifaResponseDto(
            id = entity.id!!,
            tipoBicicleta = entity.tipoBicicleta,
            valorHora = entity.valorHora,
            activa = entity.activa,
        )
}

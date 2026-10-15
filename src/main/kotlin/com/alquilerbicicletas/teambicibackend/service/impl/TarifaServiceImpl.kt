package com.alquilerbicicletas.teambicibackend.service.impl

import com.alquilerbicicletas.teambicibackend.api.dto.tarifa.TarifaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.tarifa.TarifaResponseDto
import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.exception.ResourceNotFoundException
import com.alquilerbicicletas.teambicibackend.mapper.TarifaMapper
import com.alquilerbicicletas.teambicibackend.repository.TarifaRepository
import com.alquilerbicicletas.teambicibackend.service.TarifaService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** Implementa los casos de uso de tarifas. */
@Service
class TarifaServiceImpl(private val repository: TarifaRepository, private val mapper: TarifaMapper) : TarifaService {
    @Transactional
    override fun crear(dto: TarifaRequestDto): TarifaResponseDto {
        if (repository.findByTipoBicicleta(dto.tipoBicicleta) != null) throw BusinessException("Ya existe una tarifa para este tipo")
        return mapper.toResponse(repository.save(mapper.toEntity(dto)))
    }
    @Transactional(readOnly = true)
    override fun listar() = repository.findAll().map(mapper::toResponse)
    @Transactional
    override fun actualizar(id: Long, dto: TarifaRequestDto): TarifaResponseDto {
        val entity = repository.findById(id).orElseThrow { ResourceNotFoundException("Tarifa no encontrada") }
        val sameType = repository.findByTipoBicicleta(dto.tipoBicicleta)
        if (sameType != null && sameType.id != id) throw BusinessException("Ya existe una tarifa para este tipo")
        mapper.updateEntity(dto, entity)
        return mapper.toResponse(entity)
    }
}

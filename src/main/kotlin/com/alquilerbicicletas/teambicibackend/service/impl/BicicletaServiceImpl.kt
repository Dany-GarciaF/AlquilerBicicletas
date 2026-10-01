package com.alquilerbicicletas.teambicibackend.service.impl

import com.alquilerbicicletas.teambicibackend.api.dto.bicicleta.BicicletaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.bicicleta.BicicletaResponseDto
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta
import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.exception.ResourceNotFoundException
import com.alquilerbicicletas.teambicibackend.mapper.BicicletaMapper
import com.alquilerbicicletas.teambicibackend.repository.BicicletaRepository
import com.alquilerbicicletas.teambicibackend.repository.EstacionRepository
import com.alquilerbicicletas.teambicibackend.service.BicicletaService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BicicletaServiceImpl(private val bicicletas: BicicletaRepository, private val estaciones: EstacionRepository, private val mapper: BicicletaMapper) : BicicletaService {
    @Transactional
    override fun crear(dto: BicicletaRequestDto): BicicletaResponseDto {
        if (bicicletas.existsByCodigo(dto.codigo.trim().uppercase())) throw BusinessException("El código de bicicleta ya existe")
        return mapper.toResponse(bicicletas.save(mapper.toEntity(dto, estacion(dto.estacionId))))
    }
    @Transactional(readOnly = true)
    override fun listar() = bicicletas.findAll().map(mapper::toResponse)
    @Transactional(readOnly = true)
    override fun obtener(id: Long) = mapper.toResponse(bicicleta(id))
    @Transactional
    override fun actualizar(id: Long, dto: BicicletaRequestDto): BicicletaResponseDto {
        val entity = bicicleta(id)
        if (entity.codigo != dto.codigo.trim().uppercase() && bicicletas.existsByCodigo(dto.codigo.trim().uppercase())) throw BusinessException("El código de bicicleta ya existe")
        if (entity.estado == EstadoBicicleta.ALQUILADA) throw BusinessException("No se puede modificar una bicicleta alquilada")
        mapper.updateEntity(dto, entity, estacion(dto.estacionId))
        return mapper.toResponse(entity)
    }
    @Transactional
    override fun cambiarEstado(id: Long, estado: EstadoBicicleta): BicicletaResponseDto {
        val entity = bicicleta(id)
        if (entity.estado == EstadoBicicleta.ALQUILADA && estado != EstadoBicicleta.ALQUILADA) throw BusinessException("La devolución debe finalizar el alquiler")
        entity.estado = estado
        return mapper.toResponse(entity)
    }
    @Transactional(readOnly = true)
    override fun disponibles(estacionId: Long?) =
        (if (estacionId == null) {
            bicicletas.findByEstado(EstadoBicicleta.DISPONIBLE)
        } else {
            bicicletas.findByEstacionActualIdAndEstado(estacionId, EstadoBicicleta.DISPONIBLE)
        }).map(mapper::toResponse)
    private fun bicicleta(id: Long) = bicicletas.findById(id).orElseThrow { ResourceNotFoundException("Bicicleta no encontrada") }
    private fun estacion(id: Long) = estaciones.findById(id).orElseThrow { ResourceNotFoundException("Estación no encontrada") }
}

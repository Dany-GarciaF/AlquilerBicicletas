package com.alquilerbicicletas.teambicibackend.service.impl

import com.alquilerbicicletas.teambicibackend.api.dto.estacion.EstacionRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.estacion.EstacionResponseDto
import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.exception.ResourceNotFoundException
import com.alquilerbicicletas.teambicibackend.mapper.EstacionMapper
import com.alquilerbicicletas.teambicibackend.repository.EstacionRepository
import com.alquilerbicicletas.teambicibackend.service.EstacionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** Implementa los casos de uso de estaciones. */
@Service
class EstacionServiceImpl(private val repository: EstacionRepository, private val mapper: EstacionMapper) : EstacionService {
    @Transactional
    override fun crear(dto: EstacionRequestDto): EstacionResponseDto {
        if (repository.existsByCodigo(dto.codigo.trim().uppercase())) throw BusinessException("El código de estación ya existe")
        return mapper.toResponse(repository.save(mapper.toEntity(dto)))
    }
    @Transactional(readOnly = true)
    override fun listar() = repository.findAll().map(mapper::toResponse)
    @Transactional(readOnly = true)
    override fun obtener(id: Long) = mapper.toResponse(entidad(id))
    @Transactional
    override fun actualizar(id: Long, dto: EstacionRequestDto): EstacionResponseDto {
        val entity = entidad(id)
        if (entity.codigo != dto.codigo.trim().uppercase() && repository.existsByCodigo(dto.codigo.trim().uppercase())) throw BusinessException("El código de estación ya existe")
        mapper.updateEntity(dto, entity)
        return mapper.toResponse(entity)
    }
    @Transactional
    override fun eliminar(id: Long) = repository.delete(entidad(id))
    private fun entidad(id: Long) = repository.findById(id).orElseThrow { ResourceNotFoundException("Estación no encontrada") }
}

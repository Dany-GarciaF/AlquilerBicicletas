package com.alquilerbicicletas.teambicibackend.service.impl

import com.alquilerbicicletas.teambicibackend.api.dto.reserva.ReservaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.reserva.ReservaResponseDto
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoReserva
import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.exception.ResourceNotFoundException
import com.alquilerbicicletas.teambicibackend.mapper.ReservaMapper
import com.alquilerbicicletas.teambicibackend.repository.BicicletaRepository
import com.alquilerbicicletas.teambicibackend.repository.EstacionRepository
import com.alquilerbicicletas.teambicibackend.repository.ReservaRepository
import com.alquilerbicicletas.teambicibackend.repository.UsuarioRepository
import com.alquilerbicicletas.teambicibackend.service.ReservaService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ReservaServiceImpl(
    private val reservas: ReservaRepository, private val usuarios: UsuarioRepository, private val bicicletas: BicicletaRepository,
    private val estaciones: EstacionRepository, private val mapper: ReservaMapper,
) : ReservaService {
    @Transactional
    override fun crear(usuarioId: Long, dto: ReservaRequestDto): ReservaResponseDto {
        if (!dto.fechaFin.isAfter(dto.fechaInicio) || !dto.fechaInicio.isAfter(Instant.now())) throw BusinessException("El periodo de reserva no es válido")
        val usuario = usuarios.findById(usuarioId).orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        val bicicleta = bicicletas.findById(dto.bicicletaId).orElseThrow { ResourceNotFoundException("Bicicleta no encontrada") }
        val estacion = estaciones.findById(dto.estacionRetiroId).orElseThrow { ResourceNotFoundException("Estación no encontrada") }
        if (!usuario.activo || bicicleta.estado != EstadoBicicleta.DISPONIBLE || bicicleta.estacionActual.id != estacion.id) throw BusinessException("La bicicleta no está disponible en la estación seleccionada")
        if (reservas.countSolapamientos(bicicleta.id!!, listOf(EstadoReserva.CONFIRMADA), dto.fechaInicio, dto.fechaFin) > 0) throw BusinessException("La bicicleta ya tiene una reserva en ese periodo")
        return mapper.toResponse(reservas.save(mapper.toEntity(dto, usuario, bicicleta, estacion)))
    }
    @Transactional(readOnly = true)
    override fun listarPorUsuario(usuarioId: Long) = reservas.findByUsuarioIdOrderByFechaInicioDesc(usuarioId).map(mapper::toResponse)
    @Transactional
    override fun cancelar(id: Long, usuarioId: Long): ReservaResponseDto {
        val reserva = reservas.findById(id).orElseThrow { ResourceNotFoundException("Reserva no encontrada") }
        if (reserva.usuario.id != usuarioId) throw BusinessException("No puede cancelar una reserva ajena")
        if (reserva.estado != EstadoReserva.CONFIRMADA || !reserva.fechaInicio.isAfter(Instant.now())) throw BusinessException("La reserva ya no puede cancelarse")
        reserva.estado = EstadoReserva.CANCELADA
        return mapper.toResponse(reserva)
    }
}

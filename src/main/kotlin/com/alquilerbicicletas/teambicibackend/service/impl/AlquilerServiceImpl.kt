package com.alquilerbicicletas.teambicibackend.service.impl

import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.AlquilerRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.AlquilerResponseDto
import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.DevolucionRequestDto
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoAlquiler
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta
import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.exception.ResourceNotFoundException
import com.alquilerbicicletas.teambicibackend.mapper.AlquilerMapper
import com.alquilerbicicletas.teambicibackend.repository.*
import com.alquilerbicicletas.teambicibackend.service.AlquilerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant

@Service
class AlquilerServiceImpl(
    private val alquileres: AlquilerRepository, private val usuarios: UsuarioRepository, private val bicicletas: BicicletaRepository,
    private val estaciones: EstacionRepository, private val tarifas: TarifaRepository, private val mapper: AlquilerMapper,
) : AlquilerService {
    @Transactional
    override fun crear(usuarioId: Long, dto: AlquilerRequestDto): AlquilerResponseDto {
        val usuario = usuarios.findById(usuarioId).orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        val bicicleta = bicicletas.findById(dto.bicicletaId).orElseThrow { ResourceNotFoundException("Bicicleta no encontrada") }
        val estacion = estaciones.findById(dto.estacionRetiroId).orElseThrow { ResourceNotFoundException("Estación no encontrada") }
        val tarifa = tarifas.findByTipoBicicleta(bicicleta.tipo)?.takeIf { it.activa } ?: throw BusinessException("No existe una tarifa activa para esta bicicleta")
        if (bicicleta.estado != EstadoBicicleta.DISPONIBLE || bicicleta.estacionActual.id != estacion.id || alquileres.existsByBicicletaIdAndEstado(bicicleta.id!!, EstadoAlquiler.ACTIVO)) throw BusinessException("La bicicleta no está disponible")
        bicicleta.estado = EstadoBicicleta.ALQUILADA
        return mapper.toResponse(alquileres.save(mapper.toEntity(dto, usuario, bicicleta, estacion, tarifa.valorHora)))
    }
    @Transactional(readOnly = true)
    override fun listarPorUsuario(usuarioId: Long) = alquileres.findByUsuarioIdOrderByFechaInicioDesc(usuarioId).map(mapper::toResponse)
    @Transactional
    override fun devolver(id: Long, usuarioId: Long, dto: DevolucionRequestDto): AlquilerResponseDto {
        val alquiler = alquileres.findById(id).orElseThrow { ResourceNotFoundException("Alquiler no encontrado") }
        if (alquiler.usuario.id != usuarioId || alquiler.estado != EstadoAlquiler.ACTIVO) throw BusinessException("El alquiler no puede devolverse")
        val estacion = estaciones.findById(dto.estacionDevolucionId).orElseThrow { ResourceNotFoundException("Estación no encontrada") }
        if (!estacion.activa) throw BusinessException("La estación de devolución está inactiva")
        val fin = Instant.now()
        alquiler.fechaFin = fin; alquiler.estacionDevolucion = estacion; alquiler.estado = EstadoAlquiler.FINALIZADO
        alquiler.costoFinal = alquiler.tarifaHoraAplicada.multiply(Duration.between(alquiler.fechaInicio, fin).toMinutes().toBigDecimal()).divide(60.toBigDecimal(), 2, RoundingMode.HALF_UP)
        alquiler.bicicleta.estado = EstadoBicicleta.DISPONIBLE; alquiler.bicicleta.estacionActual = estacion
        return mapper.toResponse(alquiler)
    }
}

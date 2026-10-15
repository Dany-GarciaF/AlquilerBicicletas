package com.alquilerbicicletas.teambicibackend.mapper

import com.alquilerbicicletas.teambicibackend.api.dto.reserva.ReservaRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.reserva.ReservaResponseDto
import com.alquilerbicicletas.teambicibackend.domain.entity.Bicicleta
import com.alquilerbicicletas.teambicibackend.domain.entity.Estacion
import com.alquilerbicicletas.teambicibackend.domain.entity.Reserva
import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoReserva
import org.springframework.stereotype.Component

/** Convierte entre la entidad Reserva y sus DTOs. */
@Component
class ReservaMapper {
    fun toEntity(
        dto: ReservaRequestDto,
        usuario: Usuario,
        bicicleta: Bicicleta,
        estacionRetiro: Estacion,
    ): Reserva =
        Reserva(
            usuario = usuario,
            bicicleta = bicicleta,
            estacionRetiro = estacionRetiro,
            fechaInicio = dto.fechaInicio,
            fechaFin = dto.fechaFin,
            estado = EstadoReserva.CONFIRMADA,
        )

    fun toResponse(entity: Reserva): ReservaResponseDto =
        ReservaResponseDto(
            id = entity.id!!,
            usuarioId = entity.usuario.id!!,
            bicicletaId = entity.bicicleta.id!!,
            estacionRetiroId = entity.estacionRetiro.id!!,
            fechaInicio = entity.fechaInicio,
            fechaFin = entity.fechaFin,
            estado = entity.estado,
        )
}

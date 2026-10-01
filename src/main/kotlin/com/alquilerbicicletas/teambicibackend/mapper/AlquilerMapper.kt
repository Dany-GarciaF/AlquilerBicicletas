package com.alquilerbicicletas.teambicibackend.mapper

import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.AlquilerRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.AlquilerResponseDto
import com.alquilerbicicletas.teambicibackend.domain.entity.Alquiler
import com.alquilerbicicletas.teambicibackend.domain.entity.Bicicleta
import com.alquilerbicicletas.teambicibackend.domain.entity.Estacion
import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

@Component
class AlquilerMapper {
    fun toEntity(
        dto: AlquilerRequestDto,
        usuario: Usuario,
        bicicleta: Bicicleta,
        estacionRetiro: Estacion,
        tarifaHoraAplicada: BigDecimal,
    ): Alquiler =
        Alquiler(
            usuario = usuario,
            bicicleta = bicicleta,
            estacionRetiro = estacionRetiro,
            fechaInicio = Instant.now(),
            tarifaHoraAplicada = tarifaHoraAplicada,
        )

    fun toResponse(entity: Alquiler): AlquilerResponseDto =
        AlquilerResponseDto(
            id = entity.id!!,
            usuarioId = entity.usuario.id!!,
            bicicletaId = entity.bicicleta.id!!,
            estacionRetiroId = entity.estacionRetiro.id!!,
            estacionDevolucionId = entity.estacionDevolucion?.id,
            fechaInicio = entity.fechaInicio,
            fechaFin = entity.fechaFin,
            estado = entity.estado,
            tarifaHoraAplicada = entity.tarifaHoraAplicada,
            costoFinal = entity.costoFinal,
        )
}

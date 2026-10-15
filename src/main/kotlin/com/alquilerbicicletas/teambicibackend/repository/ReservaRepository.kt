package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.Reserva
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoReserva
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

/** Acceso a datos de reservas, incluida la detección de solapamientos. */
interface ReservaRepository : JpaRepository<Reserva, Long> {
    fun findByUsuarioIdOrderByFechaInicioDesc(usuarioId: Long): List<Reserva>

    @Query(
        """
        select count(r)
        from Reserva r
        where r.bicicleta.id = :bicicletaId
          and r.estado in :estados
          and r.fechaInicio < :fechaFin
          and r.fechaFin > :fechaInicio
        """,
    )
    fun countSolapamientos(
        @Param("bicicletaId") bicicletaId: Long,
        @Param("estados") estados: Collection<EstadoReserva>,
        @Param("fechaInicio") fechaInicio: Instant,
        @Param("fechaFin") fechaFin: Instant,
    ): Long
}

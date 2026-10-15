package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.Bicicleta
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta
import org.springframework.data.jpa.repository.JpaRepository

/** Acceso a datos de bicicletas, con consultas por estado y estación. */
interface BicicletaRepository : JpaRepository<Bicicleta, Long> {
    fun findByEstado(estado: EstadoBicicleta): List<Bicicleta>
    fun findByEstacionActualIdAndEstado(estacionId: Long, estado: EstadoBicicleta): List<Bicicleta>
    fun existsByCodigo(codigo: String): Boolean
}

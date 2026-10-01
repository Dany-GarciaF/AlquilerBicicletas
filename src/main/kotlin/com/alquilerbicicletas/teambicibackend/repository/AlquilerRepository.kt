package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.Alquiler
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoAlquiler
import org.springframework.data.jpa.repository.JpaRepository

interface AlquilerRepository : JpaRepository<Alquiler, Long> {
    fun findByUsuarioIdOrderByFechaInicioDesc(usuarioId: Long): List<Alquiler>
    fun existsByBicicletaIdAndEstado(bicicletaId: Long, estado: EstadoAlquiler): Boolean
}

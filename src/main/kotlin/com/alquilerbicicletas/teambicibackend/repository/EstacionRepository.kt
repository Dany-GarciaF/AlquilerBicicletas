package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.Estacion
import org.springframework.data.jpa.repository.JpaRepository

interface EstacionRepository : JpaRepository<Estacion, Long> {
    fun existsByCodigo(codigo: String): Boolean
}

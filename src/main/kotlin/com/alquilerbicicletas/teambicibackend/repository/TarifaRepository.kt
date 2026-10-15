package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.Tarifa
import com.alquilerbicicletas.teambicibackend.domain.enums.TipoBicicleta
import org.springframework.data.jpa.repository.JpaRepository

/** Acceso a datos de tarifas. */
interface TarifaRepository : JpaRepository<Tarifa, Long> {
    fun findByTipoBicicleta(tipoBicicleta: TipoBicicleta): Tarifa?
}

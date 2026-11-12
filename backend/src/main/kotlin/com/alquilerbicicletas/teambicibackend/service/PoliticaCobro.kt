package com.alquilerbicicletas.teambicibackend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration

/** Regla de cobro de los alquileres: se factura como mínimo un tiempo fijo, aunque la bicicleta se devuelva antes. */
@Component
class PoliticaCobro(
    @Value("\${team-bici.alquiler.minutos-minimos:30}") val minutosMinimos: Int,
) {
    /** Costo del alquiler: tarifa por hora proporcional al tiempo usado, con el mínimo facturable aplicado. */
    fun costo(tarifaHora: BigDecimal, uso: Duration): BigDecimal {
        val minutosFacturados = maxOf(uso.toMinutes(), minutosMinimos.toLong())
        return tarifaHora.multiply(minutosFacturados.toBigDecimal()).divide(60.toBigDecimal(), 2, RoundingMode.HALF_UP)
    }
}
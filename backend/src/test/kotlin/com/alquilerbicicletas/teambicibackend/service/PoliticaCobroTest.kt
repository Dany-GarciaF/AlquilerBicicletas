package com.alquilerbicicletas.teambicibackend.service

import java.math.BigDecimal
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

class PoliticaCobroTest {
    private val politica = PoliticaCobro(minutosMinimos = 30)
    private val tarifa = BigDecimal("10000")

    @Test
    fun `una devolucion inmediata cobra el minimo`() {
        assertEquals(BigDecimal("5000.00"), politica.costo(tarifa, Duration.ZERO))
    }

    @Test
    fun `un uso menor al minimo cobra el minimo`() {
        assertEquals(BigDecimal("5000.00"), politica.costo(tarifa, Duration.ofMinutes(12)))
    }

    @Test
    fun `un uso igual al minimo cobra el minimo`() {
        assertEquals(BigDecimal("5000.00"), politica.costo(tarifa, Duration.ofMinutes(30)))
    }

    @Test
    fun `un uso mayor al minimo se cobra proporcional`() {
        assertEquals(BigDecimal("15000.00"), politica.costo(tarifa, Duration.ofMinutes(90)))
    }
}
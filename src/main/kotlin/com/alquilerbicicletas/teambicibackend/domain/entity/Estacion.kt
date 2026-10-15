package com.alquilerbicicletas.teambicibackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

/** Entidad de una estación donde se retiran y devuelven bicicletas, con sus coordenadas. */
@Entity
@Table(name = "estaciones")
class Estacion(
    @Column(nullable = false, unique = true, length = 30)
    var codigo: String,
    @Column(nullable = false, length = 120)
    var nombre: String,
    @Column(nullable = false, length = 255)
    var direccion: String,
    @Column(nullable = false, precision = 9, scale = 6)
    var latitud: BigDecimal,
    @Column(nullable = false, precision = 9, scale = 6)
    var longitud: BigDecimal,
    @Column(nullable = false)
    var activa: Boolean = true,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

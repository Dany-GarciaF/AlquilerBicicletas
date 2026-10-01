package com.alquilerbicicletas.teambicibackend.domain.entity

import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoAlquiler
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "alquileres")
class Alquiler(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bicicleta_id", nullable = false)
    var bicicleta: Bicicleta,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_retiro_id", nullable = false)
    var estacionRetiro: Estacion,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_devolucion_id")
    var estacionDevolucion: Estacion? = null,
    @Column(name = "fecha_inicio", nullable = false)
    var fechaInicio: Instant,
    @Column(name = "fecha_fin")
    var fechaFin: Instant? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var estado: EstadoAlquiler = EstadoAlquiler.ACTIVO,
    @Column(name = "tarifa_hora_aplicada", nullable = false, precision = 12, scale = 2)
    var tarifaHoraAplicada: BigDecimal,
    @Column(name = "costo_final", precision = 12, scale = 2)
    var costoFinal: BigDecimal? = null,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

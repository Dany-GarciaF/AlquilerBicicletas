package com.alquilerbicicletas.teambicibackend.domain.entity

import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoReserva
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
import java.time.Instant

/** Entidad de una reserva de una bicicleta para un periodo futuro. */
@Entity
@Table(name = "reservas")
class Reserva(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bicicleta_id", nullable = false)
    var bicicleta: Bicicleta,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_retiro_id", nullable = false)
    var estacionRetiro: Estacion,
    @Column(name = "fecha_inicio", nullable = false)
    var fechaInicio: Instant,
    @Column(name = "fecha_fin", nullable = false)
    var fechaFin: Instant,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var estado: EstadoReserva = EstadoReserva.PENDIENTE,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

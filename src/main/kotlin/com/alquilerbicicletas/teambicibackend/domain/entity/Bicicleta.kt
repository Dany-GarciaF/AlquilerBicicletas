package com.alquilerbicicletas.teambicibackend.domain.entity

import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta
import com.alquilerbicicletas.teambicibackend.domain.enums.TipoBicicleta
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

@Entity
@Table(name = "bicicletas")
class Bicicleta(
    @Column(nullable = false, unique = true, length = 30)
    var codigo: String,
    @Column(nullable = false, length = 80)
    var marca: String,
    @Column(nullable = false, length = 80)
    var modelo: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var tipo: TipoBicicleta,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var estado: EstadoBicicleta = EstadoBicicleta.DISPONIBLE,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    var estacionActual: Estacion,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

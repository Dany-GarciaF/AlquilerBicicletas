package com.alquilerbicicletas.teambicibackend.domain.entity

import com.alquilerbicicletas.teambicibackend.domain.enums.TipoBicicleta
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "tarifas")
class Tarifa(
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_bicicleta", nullable = false, unique = true, length = 20)
    var tipoBicicleta: TipoBicicleta,
    @Column(name = "valor_hora", nullable = false, precision = 12, scale = 2)
    var valorHora: BigDecimal,
    @Column(nullable = false)
    var activa: Boolean = true,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

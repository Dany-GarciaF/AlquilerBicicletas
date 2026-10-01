package com.alquilerbicicletas.teambicibackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.Instant

@MappedSuperclass
abstract class EntidadAuditable {
    @Column(nullable = false, updatable = false)
    var creadoEn: Instant? = null

    @Column(nullable = false)
    var actualizadoEn: Instant? = null

    @PrePersist
    fun asignarFechasDeCreacion() {
        val ahora = Instant.now()
        creadoEn = ahora
        actualizadoEn = ahora
    }

    @PreUpdate
    fun actualizarFechaDeModificacion() {
        actualizadoEn = Instant.now()
    }
}

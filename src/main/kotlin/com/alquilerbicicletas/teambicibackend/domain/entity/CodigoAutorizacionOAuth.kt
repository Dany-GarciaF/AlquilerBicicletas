package com.alquilerbicicletas.teambicibackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "codigos_autorizacion_oauth")
class CodigoAutorizacionOAuth(
    @Column(name = "codigo_hash", nullable = false, unique = true, length = 64)
    var codigoHash: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario,
    @Column(name = "expira_en", nullable = false)
    var expiraEn: Instant,
    @Column(name = "usado_en")
    var usadoEn: Instant? = null,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

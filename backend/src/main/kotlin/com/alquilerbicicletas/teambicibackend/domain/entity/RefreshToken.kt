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

/** Refresh token de una sesión (se guarda su hash); los de una misma familia se rotan en cada renovación. */
@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    var tokenHash: String,
    @Column(name = "familia", nullable = false, length = 36)
    var familia: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario,
    @Column(name = "expira_en", nullable = false)
    var expiraEn: Instant,
    @Column(name = "revocado_en")
    var revocadoEn: Instant? = null,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

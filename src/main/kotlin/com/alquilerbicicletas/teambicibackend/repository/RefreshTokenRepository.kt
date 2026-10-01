package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByTokenHash(tokenHash: String): RefreshToken?

    @Modifying
    @Query("update RefreshToken t set t.revocadoEn = :now where t.familia = :familia and t.revocadoEn is null")
    fun revokeFamily(@Param("familia") familia: String, @Param("now") now: Instant): Int

    @Modifying
    @Query("update RefreshToken t set t.revocadoEn = :now where t.usuario.id = :usuarioId and t.revocadoEn is null")
    fun revokeAllForUser(@Param("usuarioId") usuarioId: Long, @Param("now") now: Instant): Int

    @Modifying
    @Query("delete from RefreshToken t where t.expiraEn < :expiredBefore or (t.revocadoEn is not null and t.revocadoEn < :revokedBefore)")
    fun deleteObsolete(@Param("expiredBefore") expiredBefore: Instant, @Param("revokedBefore") revokedBefore: Instant): Int
}

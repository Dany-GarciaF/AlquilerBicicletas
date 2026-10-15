package com.alquilerbicicletas.teambicibackend.config

import com.alquilerbicicletas.teambicibackend.repository.RefreshTokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

/** Tarea programada que elimina periódicamente los refresh tokens vencidos o revocados. */
@Component
class TokenCleanupJob(
    private val refreshTokens: RefreshTokenRepository,
) {
    @Scheduled(cron = "\${security.token-cleanup-cron:0 0 3 * * *}")
    @Transactional
    fun removeObsoleteTokens() {
        val now = Instant.now()
        refreshTokens.deleteObsolete(now, now.minus(7, ChronoUnit.DAYS))
    }
}

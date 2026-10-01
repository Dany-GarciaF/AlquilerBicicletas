package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.domain.entity.RefreshToken
import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import com.alquilerbicicletas.teambicibackend.exception.UnauthorizedException
import com.alquilerbicicletas.teambicibackend.repository.RefreshTokenRepository
import com.alquilerbicicletas.teambicibackend.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.UUID

@Service
class RefreshTokenService(
    private val tokens: RefreshTokenRepository,
    private val usuarios: UsuarioRepository,
    @Value("\${security.jwt.refresh-expiration-days:14}") private val refreshExpirationDays: Long,
) {
    @Transactional
    fun create(usuario: Usuario, family: String = UUID.randomUUID().toString()): String {
        val raw = randomToken()
        tokens.save(RefreshToken(hash(raw), family, usuario, Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS)))
        return raw
    }

    @Transactional
    fun rotate(rawToken: String): RotatedRefreshToken {
        val token = tokens.findByTokenHash(hash(rawToken)) ?: throw UnauthorizedException("El refresh token no es válido")
        if (token.revocadoEn != null || !token.expiraEn.isAfter(Instant.now())) {
            tokens.revokeFamily(token.familia, Instant.now())
            throw UnauthorizedException("El refresh token expiró o ya fue utilizado")
        }
        token.revocadoEn = Instant.now()
        return RotatedRefreshToken(
            usuarioId = token.usuario.id ?: throw UnauthorizedException("Usuario no disponible"),
            refreshToken = create(token.usuario, token.familia),
        )
    }

    @Transactional
    fun revokeAllSessions(usuarioId: Long) {
        val usuario = usuarios.findById(usuarioId).orElseThrow { UnauthorizedException("Usuario no disponible") }
        usuario.versionSesion += 1
        tokens.revokeAllForUser(usuarioId, Instant.now())
    }

    private fun randomToken(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(48).also(SecureRandom()::nextBytes))
    private fun hash(value: String): String = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(StandardCharsets.UTF_8)).joinToString("") { "%02x".format(it) }
}

data class RotatedRefreshToken(
    val usuarioId: Long,
    val refreshToken: String,
)

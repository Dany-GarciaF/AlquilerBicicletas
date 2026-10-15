package com.alquilerbicicletas.teambicibackend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.crypto.SecretKey

/** Genera y valida los tokens JWT de acceso. */
@Service
class JwtService(
    @Value("\${security.jwt.secret}") private val secret: String,
    @Value("\${security.jwt.expiration-minutes}") private val expirationMinutes: Long,
) {
    fun generarToken(usuario: UsuarioPrincipal): String {
        val ahora = Instant.now()
        return Jwts.builder()
            .subject(usuario.username)
            .claim("uid", usuario.usuarioId)
            .claim("rol", usuario.rol.name)
            .claim("sv", usuario.versionSesion)
            .issuedAt(Date.from(ahora))
            .expiration(Date.from(ahora.plus(expirationMinutes, ChronoUnit.MINUTES)))
            .signWith(claveFirma())
            .compact()
    }

    fun esTokenValido(token: String, usuario: UserDetails): Boolean {
        val claims = extraerClaims(token)
        val versionSesion = (claims["sv"] as? Number)?.toLong() ?: return false
        return claims.subject == usuario.username &&
            versionSesion == (usuario as? UsuarioPrincipal)?.versionSesion &&
            claims.expiration.after(Date())
    }

    fun extraerCorreo(token: String): String = extraerClaims(token).subject

    private fun extraerClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(claveFirma())
            .build()
            .parseSignedClaims(token)
            .payload

    private fun claveFirma(): SecretKey {
        require(secret.length >= 32) { "JWT_SECRET debe tener al menos 32 caracteres" }
        return Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }
}

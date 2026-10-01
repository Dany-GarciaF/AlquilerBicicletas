package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.api.dto.auth.AuthResponseDto
import com.alquilerbicicletas.teambicibackend.domain.entity.CodigoAutorizacionOAuth
import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import com.alquilerbicicletas.teambicibackend.domain.enums.ProveedorAuth
import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.repository.CodigoAutorizacionOAuthRepository
import com.alquilerbicicletas.teambicibackend.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64

@Service
class GoogleOAuth2Service(
    private val usuarios: UsuarioRepository,
    private val codigos: CodigoAutorizacionOAuthRepository,
    private val authService: AuthService,
    @Value("\${security.oauth2.authorization-code-ttl-seconds:60}") private val codeTtlSeconds: Long,
) {
    @Transactional
    fun crearCodigo(user: OidcUser): String {
        val correo = user.getAttribute<String>("email")?.trim()?.lowercase()
            ?: throw BusinessException("Google no proporcionó un correo electrónico")
        if (user.getAttribute<Boolean>("email_verified") != true) {
            throw BusinessException("Google no confirmó el correo electrónico de la cuenta")
        }
        val subject = user.subject
            ?: throw BusinessException("Google no proporcionó el identificador de la cuenta")
        val usuario = usuarios.findByGoogleSubject(subject)
            ?: usuarios.findByCorreo(correo)?.also { it.googleSubject = subject }
            ?: usuarios.save(
                Usuario(
                    nombre = user.getAttribute<String>("given_name")?.trim().orEmpty().ifBlank { "Usuario" },
                    apellido = user.getAttribute<String>("family_name")?.trim().orEmpty().ifBlank { "Google" },
                    correo = correo,
                    proveedorAuth = ProveedorAuth.GOOGLE,
                    proveedorId = subject,
                    googleSubject = subject,
                ),
            )
        return generarCodigo(usuario)
    }

    @Transactional
    fun intercambiarCodigo(code: String): AuthResponseDto {
        val registro = codigos.findByCodigoHashAndUsadoEnIsNull(hash(code))
            ?: throw BusinessException("El código de autorización no es válido o ya fue utilizado")
        if (!registro.expiraEn.isAfter(Instant.now())) throw BusinessException("El código de autorización ha expirado")
        registro.usadoEn = Instant.now()
        return authService.emitirToken(registro.usuario.id!!)
    }

    private fun generarCodigo(usuario: Usuario): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        val code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        codigos.save(CodigoAutorizacionOAuth(hash(code), usuario, Instant.now().plus(codeTtlSeconds, ChronoUnit.SECONDS)))
        return code
    }

    private fun hash(value: String): String =
        MessageDigest.getInstance("SHA-256").digest(value.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}

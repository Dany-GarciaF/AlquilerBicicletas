package com.alquilerbicicletas.teambicibackend.service.impl

import com.alquilerbicicletas.teambicibackend.api.dto.auth.AuthResponseDto
import com.alquilerbicicletas.teambicibackend.api.dto.auth.LoginRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.auth.RegistroRequestDto
import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.mapper.UsuarioMapper
import com.alquilerbicicletas.teambicibackend.repository.UsuarioRepository
import com.alquilerbicicletas.teambicibackend.security.JwtService
import com.alquilerbicicletas.teambicibackend.security.UsuarioPrincipal
import com.alquilerbicicletas.teambicibackend.service.AuthService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** Implementa registro, login y renovación de sesión con contraseñas cifradas, JWT y refresh tokens. */
@Service
class AuthServiceImpl(
    private val usuarios: UsuarioRepository,
    private val mapper: UsuarioMapper,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val refreshTokenService: com.alquilerbicicletas.teambicibackend.service.RefreshTokenService,
) : AuthService {
    @Transactional
    override fun registrar(dto: RegistroRequestDto): AuthResponseDto {
        val correo = dto.correo.trim().lowercase()
        if (usuarios.existsByCorreo(correo)) throw BusinessException("Ya existe un usuario con ese correo")
        val contrasenaHash = passwordEncoder.encode(dto.contrasena)
            ?: throw IllegalStateException("No se pudo generar el hash de la contraseña")
        val usuario = usuarios.save(mapper.toEntity(dto.copy(correo = correo), contrasenaHash))
        return respuesta(usuario)
    }

    override fun iniciarSesion(dto: LoginRequestDto): AuthResponseDto {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(dto.correo.trim().lowercase(), dto.contrasena),
        )
        return emitirToken((authentication.principal as UsuarioPrincipal).usuarioId)
    }

    override fun emitirToken(usuarioId: Long): AuthResponseDto =
        respuesta(
            usuarios.findById(usuarioId).orElseThrow { BusinessException("Usuario no disponible") },
        )

    @Transactional
    override fun refresh(refreshToken: String): AuthResponseDto {
        val rotatedToken = refreshTokenService.rotate(refreshToken)
        val usuario = usuarios.findById(rotatedToken.usuarioId)
            .orElseThrow { BusinessException("Usuario no disponible") }
        return respuesta(usuario, rotatedToken.refreshToken)
    }

    override fun logout(usuarioId: Long) = refreshTokenService.revokeAllSessions(usuarioId)

    private fun respuesta(usuario: com.alquilerbicicletas.teambicibackend.domain.entity.Usuario): AuthResponseDto =
        respuesta(usuario, refreshTokenService.create(usuario))

    private fun respuesta(
        usuario: com.alquilerbicicletas.teambicibackend.domain.entity.Usuario,
        refreshToken: String,
    ): AuthResponseDto = respuesta(UsuarioPrincipal.from(usuario), refreshToken)

    private fun respuesta(principal: UsuarioPrincipal, refreshToken: String): AuthResponseDto =
        AuthResponseDto(
            accessToken = jwtService.generarToken(principal),
            refreshToken = refreshToken,
            usuario = mapper.toResponse(
                usuarios.findById(principal.usuarioId).orElseThrow { BusinessException("Usuario no disponible") },
            ),
        )
}

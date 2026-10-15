package com.alquilerbicicletas.team_bici_frontend.auth

import com.alquilerbicicletas.team_bici_frontend.network.dto.AuthResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.UsuarioResponseDto
import kotlinx.serialization.Serializable

/** Sesión iniciada: tokens de acceso y renovación junto con los datos del usuario. */
@Serializable
data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val usuario: UsuarioResponseDto,
) {
    companion object {
        fun from(response: AuthResponseDto): AuthSession =
            AuthSession(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                tokenType = response.tokenType,
                usuario = response.usuario,
            )
    }
}

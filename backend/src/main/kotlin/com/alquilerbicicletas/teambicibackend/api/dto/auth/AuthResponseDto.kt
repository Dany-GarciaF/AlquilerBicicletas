package com.alquilerbicicletas.teambicibackend.api.dto.auth

import com.alquilerbicicletas.teambicibackend.api.dto.usuario.UsuarioResponseDto

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val usuario: UsuarioResponseDto,
)

package com.alquilerbicicletas.team_bici_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequestDto(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
)

@Serializable
data class LoginRequestDto(
    val correo: String,
    val contrasena: String,
)

@Serializable
data class RefreshTokenRequestDto(
    val refreshToken: String,
)

@Serializable
data class OAuthCodeExchangeRequestDto(
    val code: String,
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val usuario: UsuarioResponseDto,
)

@Serializable
data class UsuarioResponseDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val proveedorAuth: ProveedorAuth,
    val rol: Rol,
    val activo: Boolean,
    val creadoEn: String,
)

@Serializable
enum class ProveedorAuth {
    LOCAL,
    GOOGLE,
}

@Serializable
enum class Rol {
    CLIENTE,
    ADMINISTRADOR,
}

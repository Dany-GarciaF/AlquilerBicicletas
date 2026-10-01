package com.alquilerbicicletas.teambicibackend.service

import com.alquilerbicicletas.teambicibackend.api.dto.auth.AuthResponseDto
import com.alquilerbicicletas.teambicibackend.api.dto.auth.LoginRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.auth.RegistroRequestDto

interface AuthService {
    fun registrar(dto: RegistroRequestDto): AuthResponseDto
    fun iniciarSesion(dto: LoginRequestDto): AuthResponseDto
    fun emitirToken(usuarioId: Long): AuthResponseDto
    fun refresh(refreshToken: String): AuthResponseDto
    fun logout(usuarioId: Long)
}

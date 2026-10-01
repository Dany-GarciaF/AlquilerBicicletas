package com.alquilerbicicletas.teambicibackend.api.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:Email
    @field:NotBlank
    val correo: String,
    @field:NotBlank
    val contrasena: String,
)

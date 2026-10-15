package com.alquilerbicicletas.teambicibackend.api.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegistroRequestDto(
    @field:NotBlank
    @field:Size(max = 100)
    val nombre: String,
    @field:NotBlank
    @field:Size(max = 100)
    val apellido: String,
    @field:Email
    @field:NotBlank
    @field:Size(max = 254)
    val correo: String,
    @field:NotBlank
    @field:Size(min = 8, max = 72)
    val contrasena: String,
)

package com.alquilerbicicletas.teambicibackend.api.dto.auth

import jakarta.validation.constraints.NotBlank

data class OAuthCodeExchangeRequestDto(
    @field:NotBlank
    val code: String,
)

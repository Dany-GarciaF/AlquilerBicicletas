package com.alquilerbicicletas.teambicibackend.api.dto.auth

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequestDto(@field:NotBlank val refreshToken: String)

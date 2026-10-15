package com.alquilerbicicletas.teambicibackend.exception

import java.time.Instant

/** Formato JSON uniforme con el que la API responde los errores. */
data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val errors: Map<String, String>? = null,
    val path: String,
)

package com.alquilerbicicletas.team_bici_frontend.network

/** Error devuelto por la API, con su código, mensaje y errores por campo. */
class ApiRequestException(
    val status: Int,
    override val message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
) : RuntimeException(message)

package com.alquilerbicicletas.team_bici_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: String,
    val errors: Map<String, String>? = null,
    val path: String,
)

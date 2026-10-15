package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.network.dto.ReservaRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.ReservaResponseDto
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/** Llamadas a la API de reservas. */
class ReservaRepository(private val api: ApiHttpClient) {
    suspend fun listMine(): List<ReservaResponseDto> = api.client.get(PATH).bodyOrThrow()

    suspend fun create(request: ReservaRequestDto): ReservaResponseDto =
        api.client.post(PATH) { setBody(request) }.bodyOrThrow()

    suspend fun cancel(id: Long): ReservaResponseDto =
        api.client.patch("$PATH/$id/cancelar").bodyOrThrow()

    private companion object {
        const val PATH = "/api/reservas"
    }
}

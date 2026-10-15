package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.network.dto.AlquilerRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.AlquilerResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.DevolucionRequestDto
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/** Llamadas a la API de alquileres. */
class AlquilerRepository(private val api: ApiHttpClient) {
    suspend fun listMine(): List<AlquilerResponseDto> = api.client.get(PATH).bodyOrThrow()

    suspend fun create(request: AlquilerRequestDto): AlquilerResponseDto =
        api.client.post(PATH) { setBody(request) }.bodyOrThrow()

    suspend fun returnBike(id: Long, request: DevolucionRequestDto): AlquilerResponseDto =
        api.client.patch("$PATH/$id/devolucion") { setBody(request) }.bodyOrThrow()

    private companion object {
        const val PATH = "/api/alquileres"
    }
}

package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.network.dto.TarifaRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.TarifaResponseDto
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/** Llamadas a la API de tarifas. */
class TarifaRepository(private val api: ApiHttpClient) {
    suspend fun list(): List<TarifaResponseDto> = api.client.get(PATH).bodyOrThrow()

    suspend fun create(request: TarifaRequestDto): TarifaResponseDto =
        api.client.post(PATH) { setBody(request) }.bodyOrThrow()

    suspend fun update(id: Long, request: TarifaRequestDto): TarifaResponseDto =
        api.client.put("$PATH/$id") { setBody(request) }.bodyOrThrow()

    private companion object {
        const val PATH = "/api/tarifas"
    }
}

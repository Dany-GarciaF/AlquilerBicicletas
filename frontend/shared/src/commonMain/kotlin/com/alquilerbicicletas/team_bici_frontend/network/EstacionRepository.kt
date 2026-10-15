package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/** Llamadas a la API de estaciones. */
class EstacionRepository(private val api: ApiHttpClient) {
    suspend fun list(): List<EstacionResponseDto> = api.client.get(PATH).bodyOrThrow()

    suspend fun get(id: Long): EstacionResponseDto = api.client.get("$PATH/$id").bodyOrThrow()

    suspend fun create(request: EstacionRequestDto): EstacionResponseDto =
        api.client.post(PATH) { setBody(request) }.bodyOrThrow()

    suspend fun update(id: Long, request: EstacionRequestDto): EstacionResponseDto =
        api.client.put("$PATH/$id") { setBody(request) }.bodyOrThrow()

    suspend fun delete(id: Long) {
        api.client.delete("$PATH/$id").ensureSuccess()
    }

    private companion object {
        const val PATH = "/api/estaciones"
    }
}

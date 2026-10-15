package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstadoBicicleta
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/** Llamadas a la API de bicicletas, incluida la subida y eliminación de su imagen. */
class BicicletaRepository(private val api: ApiHttpClient) {
    suspend fun list(): List<BicicletaResponseDto> = api.client.get(PATH).bodyOrThrow()

    suspend fun listAvailable(estacionId: Long? = null): List<BicicletaResponseDto> =
        api.client.get("$PATH/disponibles") {
            estacionId?.let { parameter("estacionId", it) }
        }.bodyOrThrow()

    suspend fun get(id: Long): BicicletaResponseDto = api.client.get("$PATH/$id").bodyOrThrow()

    suspend fun create(request: BicicletaRequestDto): BicicletaResponseDto =
        api.client.post(PATH) { setBody(request) }.bodyOrThrow()

    suspend fun update(id: Long, request: BicicletaRequestDto): BicicletaResponseDto =
        api.client.put("$PATH/$id") { setBody(request) }.bodyOrThrow()

    suspend fun changeStatus(id: Long, status: EstadoBicicleta): BicicletaResponseDto =
        api.client.patch("$PATH/$id/estado") {
            parameter("estado", status.name)
        }.bodyOrThrow()

    suspend fun uploadImage(id: Long, image: ByteArray, fileName: String, contentType: String): BicicletaResponseDto =
        api.client.submitFormWithBinaryData(
            url = "$PATH/$id/imagen",
            formData = formData {
                append("archivo", image, Headers.build {
                    append(HttpHeaders.ContentType, contentType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            },
        ).bodyOrThrow()

    suspend fun deleteImage(id: Long): BicicletaResponseDto = api.client.delete("$PATH/$id/imagen").bodyOrThrow()

    private companion object {
        const val PATH = "/api/bicicletas"
    }
}

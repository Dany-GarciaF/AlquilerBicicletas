package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.network.dto.ApiErrorResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

private val errorJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

internal suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    if (status.isSuccess()) return body()
    throw toApiRequestException()
}

suspend fun HttpResponse.ensureSuccess() {
    if (!status.isSuccess()) throw toApiRequestException()
}

private suspend fun HttpResponse.toApiRequestException(): ApiRequestException {
    val payload = bodyAsText()
    val apiError = try {
        errorJson.decodeFromString<ApiErrorResponse>(payload)
    } catch (_: SerializationException) {
        null
    }
    return ApiRequestException(
        status = status.value,
        message = apiError?.message ?: "La solicitud no pudo completarse",
        fieldErrors = apiError?.errors.orEmpty(),
    )
}

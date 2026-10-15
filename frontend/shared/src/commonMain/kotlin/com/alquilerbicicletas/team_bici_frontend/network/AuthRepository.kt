package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.auth.SessionManager
import com.alquilerbicicletas.team_bici_frontend.network.dto.AuthResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.LoginRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.OAuthCodeExchangeRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.RegistroRequestDto
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/** Llamadas a la API de autenticación; guarda la sesión resultante en el SessionManager. */
class AuthRepository(
    private val apiHttpClient: ApiHttpClient,
    private val sessionManager: SessionManager,
) {
    suspend fun register(request: RegistroRequestDto): AuthResponseDto =
        authenticate { apiHttpClient.client.post("/api/auth/registro") { setBody(request) } }

    suspend fun login(request: LoginRequestDto): AuthResponseDto =
        authenticate { apiHttpClient.client.post("/api/auth/login") { setBody(request) } }

    suspend fun exchangeGoogleCode(request: OAuthCodeExchangeRequestDto): AuthResponseDto =
        authenticate { apiHttpClient.client.post("/api/auth/oauth2/exchange") { setBody(request) } }

    suspend fun logout(): Boolean {
        val response = apiHttpClient.client.post("/api/auth/logout")
        response.ensureSuccess()
        sessionManager.clear()
        return true
    }

    private suspend fun authenticate(
        request: suspend () -> io.ktor.client.statement.HttpResponse,
    ): AuthResponseDto {
        return request().bodyOrThrow<AuthResponseDto>().also(sessionManager::save)
    }
}

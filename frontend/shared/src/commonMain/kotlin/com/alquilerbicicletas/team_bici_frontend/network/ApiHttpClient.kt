package com.alquilerbicicletas.team_bici_frontend.network

import com.alquilerbicicletas.team_bici_frontend.auth.SessionManager
import com.alquilerbicicletas.team_bici_frontend.config.ApiConfiguration
import com.alquilerbicicletas.team_bici_frontend.network.dto.AuthResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.RefreshTokenRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Cliente HTTP único de la app: serializa JSON, adjunta el token Bearer y lo renueva al recibir 401. */
class ApiHttpClient(
    private val sessionManager: SessionManager,
    baseUrl: String = ApiConfiguration.baseUrl,
) {
    private val refreshClient = HttpClient {
        expectSuccess = false
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    isLenient = false
                },
            )
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }

    val client = HttpClient {
        expectSuccess = false

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    isLenient = false
                },
            )
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
        install(Auth) {
            bearer {
                loadTokens {
                    sessionManager.current()?.let {
                        BearerTokens(it.accessToken, it.refreshToken)
                    }
                }
                refreshTokens {
                    val activeSession = sessionManager.current() ?: return@refreshTokens null
                    val response = refreshClient.post(REFRESH_PATH) {
                        setBody(RefreshTokenRequestDto(activeSession.refreshToken))
                    }
                    if (!response.status.isSuccess()) {
                        sessionManager.clear()
                        return@refreshTokens null
                    }

                    val refreshedSession = response.body<AuthResponseDto>()
                    sessionManager.save(refreshedSession)
                    BearerTokens(refreshedSession.accessToken, refreshedSession.refreshToken)
                }
                sendWithoutRequest { request ->
                    request.url.build().encodedPath !in UNAUTHENTICATED_PATHS
                }
            }
        }
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }

    fun close() {
        client.close()
        refreshClient.close()
    }

    private companion object {
        const val REFRESH_PATH = "/api/auth/refresh"
        val UNAUTHENTICATED_PATHS = setOf(
            "/api/auth/login",
            "/api/auth/registro",
            "/api/auth/refresh",
            "/api/auth/oauth2/exchange",
        )
    }
}

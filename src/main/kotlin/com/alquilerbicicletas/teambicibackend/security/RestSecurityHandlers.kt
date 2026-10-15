package com.alquilerbicicletas.teambicibackend.security

import com.alquilerbicicletas.teambicibackend.exception.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

/** Responde 401 en JSON cuando se accede sin autenticación. */
@Component
class RestAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) = write(response, HttpStatus.UNAUTHORIZED, "Debe autenticarse para acceder a este recurso", request)

    private fun write(
        response: HttpServletResponse,
        status: HttpStatus,
        message: String,
        request: HttpServletRequest,
    ) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(response.outputStream, ErrorResponse(status.value(), message, path = request.requestURI))
    }
}

/** Responde 403 en JSON cuando el usuario no tiene permisos suficientes. */
@Component
class RestAccessDeniedHandler(
    private val objectMapper: ObjectMapper,
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AccessDeniedException,
    ) {
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(
            response.outputStream,
            ErrorResponse(HttpStatus.FORBIDDEN.value(), "No tiene permisos para realizar esta operación", path = request.requestURI),
        )
    }
}

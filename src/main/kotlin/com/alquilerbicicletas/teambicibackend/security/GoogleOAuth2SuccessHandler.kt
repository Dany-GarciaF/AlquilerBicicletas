package com.alquilerbicicletas.teambicibackend.security

import com.alquilerbicicletas.teambicibackend.service.GoogleOAuth2Service
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
@Profile("oauth2")
class GoogleOAuth2SuccessHandler(
    private val googleOAuth2Service: GoogleOAuth2Service,
    @Value("\${security.oauth2.frontend-success-url}") private val frontendSuccessUrl: String,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val code = googleOAuth2Service.crearCodigo(authentication.principal as OidcUser)
        val destination = UriComponentsBuilder.fromUriString(frontendSuccessUrl)
            .queryParam("code", code)
            .build()
            .encode()
            .toUriString()
        response.sendRedirect(destination)
    }
}

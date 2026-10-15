package com.alquilerbicicletas.teambicibackend.security

import com.alquilerbicicletas.teambicibackend.exception.BusinessException
import com.alquilerbicicletas.teambicibackend.service.GoogleOAuth2Service
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

/** Tras el login con Google, redirige al frontend (web o móvil) con un código de un solo uso o con el error. */
@Component
@Profile("oauth2")
class GoogleOAuth2SuccessHandler(
    private val googleOAuth2Service: GoogleOAuth2Service,
    @Value("\${security.oauth2.frontend-success-url}") private val frontendSuccessUrl: String,
    @Value("\${security.oauth2.mobile-success-url:teambici://auth/google/callback}") private val mobileSuccessUrl: String,
) : AuthenticationSuccessHandler, AuthenticationFailureHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val target = targetUrl(request)
        try {
            val code = googleOAuth2Service.crearCodigo(authentication.principal as OidcUser)
            response.sendRedirect(redirect(target, "code", code))
        } catch (_: BusinessException) {
            response.sendRedirect(redirect(target, "error", GOOGLE_AUTH_FAILED))
        }
    }

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        response.sendRedirect(redirect(targetUrl(request), "error", GOOGLE_AUTH_FAILED))
    }

    private fun targetUrl(request: HttpServletRequest): String =
        if (OAuthClientType.isMobile(request.getParameter("state"))) mobileSuccessUrl else frontendSuccessUrl

    private fun redirect(target: String, name: String, value: String): String =
        UriComponentsBuilder.fromUriString(target)
            .queryParam(name, value)
            .build()
            .encode()
            .toUriString()

    private companion object {
        const val GOOGLE_AUTH_FAILED = "google_auth_failed"
    }
}

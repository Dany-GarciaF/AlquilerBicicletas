package com.alquilerbicicletas.teambicibackend.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component

/**
 * Marks the OAuth2 `state` when the login was started by the mobile app (`?client=mobile`), so the
 * success/failure handlers can redirect to the mobile deep link. The marker travels inside `state`,
 * which Spring validates against the stored request, so it cannot be tampered with.
 */
object OAuthClientType {
    const val MOBILE_PARAMETER = "client"
    const val MOBILE_VALUE = "mobile"
    const val MOBILE_STATE_SUFFIX = ".mobile"

    fun isMobile(state: String?): Boolean = state?.endsWith(MOBILE_STATE_SUFFIX) == true
}

/** Marca la solicitud OAuth2 cuando el login lo inicia la app móvil, para redirigir luego al deep link correcto. */
@Component
@Profile("oauth2")
class ClientAwareAuthorizationRequestResolver(
    clientRegistrations: ClientRegistrationRepository,
) : OAuth2AuthorizationRequestResolver {
    private val delegate = DefaultOAuth2AuthorizationRequestResolver(clientRegistrations, "/oauth2/authorization")

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? =
        mark(delegate.resolve(request), request)

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String): OAuth2AuthorizationRequest? =
        mark(delegate.resolve(request, clientRegistrationId), request)

    private fun mark(authorization: OAuth2AuthorizationRequest?, request: HttpServletRequest): OAuth2AuthorizationRequest? {
        if (authorization == null || request.getParameter(OAuthClientType.MOBILE_PARAMETER) != OAuthClientType.MOBILE_VALUE) {
            return authorization
        }
        return OAuth2AuthorizationRequest.from(authorization)
            .state(authorization.state + OAuthClientType.MOBILE_STATE_SUFFIX)
            .build()
    }
}

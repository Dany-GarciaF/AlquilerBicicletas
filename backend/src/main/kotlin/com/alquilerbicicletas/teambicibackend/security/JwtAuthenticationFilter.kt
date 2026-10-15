package com.alquilerbicicletas.teambicibackend.security

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/** Filtro que lee el token Bearer de cada petición y autentica al usuario. */
@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorization = request.getHeader("Authorization")
        if (authorization?.startsWith("Bearer ") != true) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorization.removePrefix("Bearer ").trim()
        try {
            val correo = jwtService.extraerCorreo(token)
            if (SecurityContextHolder.getContext().authentication == null) {
                val usuario = userDetailsService.loadUserByUsername(correo)
                if (jwtService.esTokenValido(token, usuario)) {
                    val authentication = UsernamePasswordAuthenticationToken(usuario, null, usuario.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    log.warn("JWT rechazado por sesión inválida en {}", request.requestURI)
                }
            }
        } catch (exception: JwtException) {
            log.warn("JWT rechazado en {}: {}", request.requestURI, exception.message)
            SecurityContextHolder.clearContext()
        } catch (exception: IllegalArgumentException) {
            log.warn("JWT no procesable en {}: {}", request.requestURI, exception.message)
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}

package com.alquilerbicicletas.teambicibackend.security

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.ObjectProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/** Configuración de seguridad: API sin estado con JWT, rutas públicas, roles y login con Google. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customUserDetailsService: CustomUserDetailsService,
    private val authenticationEntryPoint: RestAuthenticationEntryPoint,
    private val accessDeniedHandler: RestAccessDeniedHandler,
    private val googleOAuth2SuccessHandler: ObjectProvider<GoogleOAuth2SuccessHandler>,
    private val authorizationRequestResolver: ObjectProvider<ClientAwareAuthorizationRequestResolver>,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors {}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/registro", "/api/auth/login", "/api/auth/refresh", "/api/auth/oauth2/**", "/oauth2/**", "/login/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/bicicletas/disponibles", "/api/estaciones", "/api/estaciones/*", "/api/tarifas", "/uploads/**").permitAll()
                    .anyRequest().authenticated()
            }
            .authenticationProvider(daoAuthenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        googleOAuth2SuccessHandler.ifAvailable?.let { handler ->
            http.oauth2Login { login ->
                login.successHandler(handler).failureHandler(handler)
                authorizationRequestResolver.ifAvailable?.let { resolver ->
                    login.authorizationEndpoint { it.authorizationRequestResolver(resolver) }
                }
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    private fun daoAuthenticationProvider(): DaoAuthenticationProvider =
        DaoAuthenticationProvider(customUserDetailsService).also {
            it.setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun authenticationManager(): AuthenticationManager = ProviderManager(daoAuthenticationProvider())
}

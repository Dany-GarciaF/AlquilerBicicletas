package com.alquilerbicicletas.teambicibackend.security

import com.alquilerbicicletas.teambicibackend.repository.UsuarioRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val usuarioRepository: UsuarioRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        usuarioRepository.findByCorreo(username.lowercase())
            ?.let(UsuarioPrincipal::from)
            ?: throw UsernameNotFoundException("Usuario no encontrado")
}

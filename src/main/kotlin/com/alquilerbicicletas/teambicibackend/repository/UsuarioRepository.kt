package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import org.springframework.data.jpa.repository.JpaRepository

/** Acceso a datos de usuarios. */
interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByCorreo(correo: String): Usuario?
    fun findByGoogleSubject(googleSubject: String): Usuario?
    fun existsByCorreo(correo: String): Boolean
}

package com.alquilerbicicletas.teambicibackend.config

import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import com.alquilerbicicletas.teambicibackend.domain.enums.ProveedorAuth
import com.alquilerbicicletas.teambicibackend.domain.enums.Rol
import com.alquilerbicicletas.teambicibackend.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

/** Crea el usuario administrador inicial al arrancar, a partir de ADMIN_EMAIL y ADMIN_PASSWORD, si aún no existe. */
@Configuration
class BootstrapAdminConfig {
    @Bean
    fun bootstrapAdmin(
        usuarios: UsuarioRepository,
        passwordEncoder: PasswordEncoder,
        @Value("\${team-bici.bootstrap-admin.email}") email: String,
        @Value("\${team-bici.bootstrap-admin.password}") password: String,
        @Value("\${team-bici.bootstrap-admin.nombre}") nombre: String,
        @Value("\${team-bici.bootstrap-admin.apellido}") apellido: String,
    ): ApplicationRunner =
        ApplicationRunner {
            if (email.isBlank() || password.isBlank() || usuarios.existsByCorreo(email.trim().lowercase())) return@ApplicationRunner
            val hash = passwordEncoder.encode(password)
                ?: throw IllegalStateException("No se pudo generar el hash del administrador inicial")
            usuarios.save(
                Usuario(
                    nombre = nombre.trim(),
                    apellido = apellido.trim(),
                    correo = email.trim().lowercase(),
                    contrasenaHash = hash,
                    proveedorAuth = ProveedorAuth.LOCAL,
                    rol = Rol.ADMINISTRADOR,
                ),
            )
        }
}

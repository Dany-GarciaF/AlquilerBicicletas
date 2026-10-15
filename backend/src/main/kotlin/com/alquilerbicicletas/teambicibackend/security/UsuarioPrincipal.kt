package com.alquilerbicicletas.teambicibackend.security

import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import com.alquilerbicicletas.teambicibackend.domain.enums.Rol
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/** Usuario autenticado tal como lo ve Spring Security (id, correo, rol). */
class UsuarioPrincipal(
    val usuarioId: Long,
    private val correo: String,
    private val contrasenaHash: String?,
    val rol: Rol,
    val versionSesion: Long,
    private val activo: Boolean,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${rol.name}"))

    override fun getPassword(): String = contrasenaHash.orEmpty()

    override fun getUsername(): String = correo

    override fun isEnabled(): Boolean = activo

    companion object {
        fun from(usuario: Usuario): UsuarioPrincipal =
            UsuarioPrincipal(
                usuarioId = usuario.id!!,
                correo = usuario.correo,
                contrasenaHash = usuario.contrasenaHash,
                rol = usuario.rol,
                versionSesion = usuario.versionSesion,
                activo = usuario.activo,
            )
    }
}

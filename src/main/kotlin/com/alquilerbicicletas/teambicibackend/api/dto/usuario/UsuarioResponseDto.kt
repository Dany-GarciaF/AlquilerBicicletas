package com.alquilerbicicletas.teambicibackend.api.dto.usuario

import com.alquilerbicicletas.teambicibackend.domain.enums.ProveedorAuth
import com.alquilerbicicletas.teambicibackend.domain.enums.Rol
import java.time.Instant

data class UsuarioResponseDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val proveedorAuth: ProveedorAuth,
    val rol: Rol,
    val activo: Boolean,
    val creadoEn: Instant,
)

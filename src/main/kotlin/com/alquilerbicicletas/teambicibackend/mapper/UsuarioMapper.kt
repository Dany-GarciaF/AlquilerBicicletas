package com.alquilerbicicletas.teambicibackend.mapper

import com.alquilerbicicletas.teambicibackend.api.dto.auth.RegistroRequestDto
import com.alquilerbicicletas.teambicibackend.api.dto.usuario.UsuarioResponseDto
import com.alquilerbicicletas.teambicibackend.domain.entity.Usuario
import com.alquilerbicicletas.teambicibackend.domain.enums.ProveedorAuth
import org.springframework.stereotype.Component

@Component
class UsuarioMapper {
    fun toEntity(dto: RegistroRequestDto, contrasenaHash: String): Usuario =
        Usuario(
            nombre = dto.nombre.trim(),
            apellido = dto.apellido.trim(),
            correo = dto.correo.trim().lowercase(),
            contrasenaHash = contrasenaHash,
            proveedorAuth = ProveedorAuth.LOCAL,
        )

    fun toResponse(entity: Usuario): UsuarioResponseDto =
        UsuarioResponseDto(
            id = entity.id!!,
            nombre = entity.nombre,
            apellido = entity.apellido,
            correo = entity.correo,
            proveedorAuth = entity.proveedorAuth,
            rol = entity.rol,
            activo = entity.activo,
            creadoEn = entity.creadoEn!!,
        )
}

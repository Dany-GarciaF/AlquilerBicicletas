package com.alquilerbicicletas.teambicibackend.domain.entity

import com.alquilerbicicletas.teambicibackend.domain.enums.ProveedorAuth
import com.alquilerbicicletas.teambicibackend.domain.enums.Rol
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_usuario_correo", columnNames = ["correo"]),
        UniqueConstraint(name = "uk_usuario_proveedor_id", columnNames = ["proveedor_auth", "proveedor_id"]),
    ],
)
/** Entidad de un usuario: credenciales o cuenta de Google, rol y estado. */
class Usuario(
    @Column(nullable = false, length = 100)
    var nombre: String,
    @Column(nullable = false, length = 100)
    var apellido: String,
    @Column(name = "correo", nullable = false, length = 254)
    var correo: String,
    @Column(name = "contrasena_hash", length = 100)
    var contrasenaHash: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "proveedor_auth", nullable = false, length = 20)
    var proveedorAuth: ProveedorAuth,
    @Column(name = "proveedor_id", length = 255)
    var proveedorId: String? = null,
    @Column(name = "google_subject", unique = true, length = 255)
    var googleSubject: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var rol: Rol = Rol.CLIENTE,
    @Column(nullable = false)
    var activo: Boolean = true,
    @Column(name = "version_sesion", nullable = false)
    var versionSesion: Long = 0,
) : EntidadAuditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

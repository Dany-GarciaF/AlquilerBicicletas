package com.alquilerbicicletas.teambicibackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/** Imagen almacenada como binario en la base de datos, identificada por su clave de almacenamiento. */
@Entity
@Table(name = "imagenes")
class ImagenArchivo(
    @Id
    @Column(length = 200)
    var clave: String,
    @Column(name = "tipo_contenido", nullable = false, length = 50)
    var tipoContenido: String,
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(nullable = false, columnDefinition = "bytea")
    var datos: ByteArray,
)

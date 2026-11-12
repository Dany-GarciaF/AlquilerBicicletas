package com.alquilerbicicletas.teambicibackend.repository

import com.alquilerbicicletas.teambicibackend.domain.entity.ImagenArchivo
import org.springframework.data.jpa.repository.JpaRepository

/** Acceso a datos de las imágenes guardadas en la base de datos. */
interface ImagenArchivoRepository : JpaRepository<ImagenArchivo, String>

package com.alquilerbicicletas.teambicibackend.storage

import com.alquilerbicicletas.teambicibackend.domain.entity.ImagenArchivo
import com.alquilerbicicletas.teambicibackend.repository.ImagenArchivoRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/** Almacenamiento que guarda las imágenes en PostgreSQL y las publica bajo /uploads. */
@Component
@ConditionalOnProperty(name = ["storage.provider"], havingValue = "db")
class DbImageStorage(private val imagenes: ImagenArchivoRepository) : ImageStorage {
    override fun store(key: String, content: ByteArray, contentType: String): String {
        imagenes.save(ImagenArchivo(key, contentType, content))
        return "$PUBLIC_PREFIX/$key"
    }

    override fun delete(url: String) {
        if (!url.startsWith("$PUBLIC_PREFIX/")) return
        val key = url.removePrefix("$PUBLIC_PREFIX/")
        if (imagenes.existsById(key)) imagenes.deleteById(key)
    }

    companion object {
        const val PUBLIC_PREFIX = "/uploads"
    }
}

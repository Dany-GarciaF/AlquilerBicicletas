package com.alquilerbicicletas.teambicibackend.storage

import com.alquilerbicicletas.teambicibackend.repository.ImagenArchivoRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

/** Sirve públicamente las imágenes guardadas en la base de datos. */
@RestController
@ConditionalOnProperty(name = ["storage.provider"], havingValue = "db")
class ImagenArchivoController(private val imagenes: ImagenArchivoRepository) {
    @GetMapping("/uploads/**")
    fun obtener(request: HttpServletRequest): ResponseEntity<ByteArray> {
        val key = request.requestURI.removePrefix(request.contextPath).removePrefix("/uploads/")
        val imagen = imagenes.findById(key).orElse(null) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(imagen.tipoContenido))
            .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
            .body(imagen.datos)
    }
}

package com.alquilerbicicletas.teambicibackend.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/** Publica la carpeta de subidas en la ruta /uploads cuando el almacenamiento es local. */
@Configuration
@ConditionalOnProperty(name = ["storage.provider"], havingValue = "local", matchIfMissing = true)
class LocalUploadsWebConfig(@Value("\${storage.local.directory}") private val directory: String) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val location = Paths.get(directory).toAbsolutePath().normalize().toUri().toString().trimEnd('/') + "/"
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location)
            .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
    }
}
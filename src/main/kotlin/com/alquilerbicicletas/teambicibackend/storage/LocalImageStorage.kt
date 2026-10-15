package com.alquilerbicicletas.teambicibackend.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/** Development storage: files live on disk and are served by [LocalUploadsWebConfig]. */
@Component
@ConditionalOnProperty(name = ["storage.provider"], havingValue = "local", matchIfMissing = true)
class LocalImageStorage(
    @Value("\${storage.local.directory}") directory: String,
    @Value("\${storage.local.public-base-url}") publicBaseUrl: String,
) : ImageStorage {
    private val root: Path = Paths.get(directory).toAbsolutePath().normalize()
    private val baseUrl = publicBaseUrl.trimEnd('/')

    override fun store(key: String, content: ByteArray, contentType: String): String {
        val target = resolve(key)
        Files.createDirectories(target.parent)
        Files.write(target, content)
        return "$baseUrl/$key"
    }

    override fun delete(url: String) {
        if (!url.startsWith("$baseUrl/")) return
        Files.deleteIfExists(resolve(url.removePrefix("$baseUrl/")))
    }

    private fun resolve(key: String): Path =
        root.resolve(key).normalize().also { require(it.startsWith(root)) { "Invalid storage key" } }
}
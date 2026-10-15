package com.alquilerbicicletas.teambicibackend.storage

import com.alquilerbicicletas.teambicibackend.exception.BusinessException

/** Accepted image formats, detected from the file signature rather than the client-provided type. */
enum class ImageFormat(val contentType: String, val extension: String) {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp"),
}

/** Valida tamaño y formato de una imagen leyendo su firma, sin fiarse del tipo que declare el cliente. */
object ImageValidator {
    const val MAX_BYTES = 5 * 1024 * 1024

    fun detect(content: ByteArray): ImageFormat {
        if (content.isEmpty()) throw BusinessException("La imagen está vacía")
        if (content.size > MAX_BYTES) throw BusinessException("La imagen no puede superar 5 MB")
        return when {
            content.startsWith(0xFF, 0xD8, 0xFF) -> ImageFormat.JPEG
            content.startsWith(0x89, 0x50, 0x4E, 0x47) -> ImageFormat.PNG
            content.size > 12 && content.startsWith(0x52, 0x49, 0x46, 0x46) &&
                content.copyOfRange(8, 12).decodeToString() == "WEBP" -> ImageFormat.WEBP
            else -> throw BusinessException("Formato no soportado. Usa JPG, PNG o WebP")
        }
    }

    private fun ByteArray.startsWith(vararg prefix: Int) =
        size >= prefix.size && prefix.indices.all { this[it] == prefix[it].toByte() }
}
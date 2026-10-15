package com.alquilerbicicletas.teambicibackend.storage

/** Stores public images and returns the URL they can be fetched from. */
interface ImageStorage {
    /** Persists [content] under [key] and returns its public URL. */
    fun store(key: String, content: ByteArray, contentType: String): String

    /** Removes the image behind [url]; unknown or foreign URLs are ignored. */
    fun delete(url: String)
}
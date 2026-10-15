package com.alquilerbicicletas.teambicibackend.exception

/** Se lanza cuando un recurso solicitado no existe (respuesta 404). */
class ResourceNotFoundException(message: String) : RuntimeException(message)

package com.alquilerbicicletas.teambicibackend.exception

/** Se lanza cuando las credenciales o tokens no son válidos (respuesta 401). */
class UnauthorizedException(message: String) : RuntimeException(message)

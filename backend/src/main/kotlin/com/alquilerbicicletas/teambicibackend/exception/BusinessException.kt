package com.alquilerbicicletas.teambicibackend.exception

/** Se lanza cuando se incumple una regla de negocio; el manejador global la responde con 409 (conflicto). */
class BusinessException(message: String) : RuntimeException(message)

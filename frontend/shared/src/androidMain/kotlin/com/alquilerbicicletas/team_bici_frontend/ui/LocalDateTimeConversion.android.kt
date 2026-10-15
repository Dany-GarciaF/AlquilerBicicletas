package com.alquilerbicicletas.team_bici_frontend.ui

import java.time.LocalDateTime
import java.time.ZoneId

actual fun localDateTimeToUtcIso(date: String, time: String): String =
    LocalDateTime.parse("${date}T${time}:00")
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toString()

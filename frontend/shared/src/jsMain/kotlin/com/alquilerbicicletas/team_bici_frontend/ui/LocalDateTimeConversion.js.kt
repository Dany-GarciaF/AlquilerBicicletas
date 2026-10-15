package com.alquilerbicicletas.team_bici_frontend.ui

import kotlin.js.Date

actual fun localDateTimeToUtcIso(date: String, time: String): String =
    Date("${date}T${time}:00").toISOString()

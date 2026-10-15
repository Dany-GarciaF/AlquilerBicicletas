@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.alquilerbicicletas.team_bici_frontend.ui

actual fun localDateTimeToUtcIso(date: String, time: String): String = toUtcIso("${date}T${time}:00")

private fun toUtcIso(localDateTime: String): String = js("new Date(localDateTime).toISOString()")
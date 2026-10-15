package com.alquilerbicicletas.team_bici_frontend.ui

import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta
import kotlin.math.abs
import kotlin.math.round

fun formatCurrency(value: Double): String {
    val cents = round(value * 100).toLong()
    val whole = cents / 100
    val decimal = abs(cents % 100).toString().padStart(2, '0')
    return "$ $whole,$decimal"
}

fun formatDateTime(value: String): String {
    val match = Regex("""(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})""").find(value)
        ?: return value
    val (year, month, day, hour, minute) = match.destructured
    return "$day/$month/$year · $hour:$minute"
}

fun formatDate(value: String): String {
    val match = Regex("""(\d{4})-(\d{2})-(\d{2})""").find(value) ?: return value
    val (year, month, day) = match.destructured
    return "$day/$month/$year"
}

fun estadoLabel(value: String): String = value.lowercase()
    .replace('_', ' ')
    .replaceFirstChar { it.uppercase() }

fun tipoLabel(tipo: TipoBicicleta): String = when (tipo) {
    TipoBicicleta.URBANA -> "Urbana"
    TipoBicicleta.MONTANA -> "Montaña"
    TipoBicicleta.ELECTRICA -> "Eléctrica"
    TipoBicicleta.PLEGABLE -> "Plegable"
}

internal fun isoDateFromUtcMillis(millis: Long): String {
    var z = millis / 86_400_000L + 719_468L
    val era = if (z >= 0) z else z - 146_096L
    val eraValue = era / 146_097L
    val dayOfEra = z - eraValue * 146_097L
    val yearOfEra = (dayOfEra - dayOfEra / 1_460L + dayOfEra / 36_524L - dayOfEra / 146_096L) / 365L
    var year = yearOfEra + eraValue * 400L
    val dayOfYear = dayOfEra - (365L * yearOfEra + yearOfEra / 4L - yearOfEra / 100L)
    val monthPrime = (5L * dayOfYear + 2L) / 153L
    val day = dayOfYear - (153L * monthPrime + 2L) / 5L + 1L
    val month = monthPrime + if (monthPrime < 10L) 3L else -9L
    year += if (month <= 2L) 1L else 0L
    return "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

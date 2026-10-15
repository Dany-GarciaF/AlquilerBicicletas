package com.alquilerbicicletas.team_bici_frontend.ui.maps

import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto

/**
 * Google Maps deep links. They open the Google Maps website or app and need no API key.
 * Replace with an embedded map only if a Maps Platform key is provisioned.
 */
object MapLinks {
    /** Reference point of the bicycling layer link (city center). */
    private const val CITY_LAT = 4.7110
    private const val CITY_LON = -74.0721

    fun place(estacion: EstacionResponseDto): String =
        "https://www.google.com/maps/search/?api=1&query=${estacion.latitud},${estacion.longitud}"

    fun directions(estacion: EstacionResponseDto): String =
        "https://www.google.com/maps/dir/?api=1&destination=${estacion.latitud},${estacion.longitud}&travelmode=bicycling"

    /** Google Maps with the bicycling layer enabled, which draws bike lanes and routes. */
    fun bikeLayer(): String = "https://www.google.com/maps/@$CITY_LAT,$CITY_LON,12z/data=!5m1!1e3"

    fun planRoute(): String = "https://www.google.com/maps/dir/?api=1&travelmode=bicycling"
}

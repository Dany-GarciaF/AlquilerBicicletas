package com.alquilerbicicletas.team_bici_frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package com.alquilerbicicletas.team_bici_frontend

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}
package com.alquilerbicicletas.team_bici_frontend.auth

expect class SessionStorage() {
    fun read(): AuthSession?
    fun write(session: AuthSession)
    fun clear()
}

package com.alquilerbicicletas.team_bici_frontend.auth

actual class SessionStorage {
    private var session: AuthSession? = null

    actual fun read(): AuthSession? = session

    actual fun write(session: AuthSession) {
        this.session = session
    }

    actual fun clear() {
        session = null
    }
}

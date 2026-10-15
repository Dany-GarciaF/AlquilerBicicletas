package com.alquilerbicicletas.team_bici_frontend.auth

import com.alquilerbicicletas.team_bici_frontend.network.dto.AuthResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Mantiene la sesión actual como estado observable y la persiste mediante SessionStorage. */
class SessionManager(
    private val storage: SessionStorage,
) {
    private val mutableSession = MutableStateFlow(storage.read())

    val session: StateFlow<AuthSession?> = mutableSession.asStateFlow()

    fun current(): AuthSession? = mutableSession.value

    fun save(response: AuthResponseDto) = save(AuthSession.from(response))

    fun save(session: AuthSession) {
        storage.write(session)
        mutableSession.value = session
    }

    fun clear() {
        storage.clear()
        mutableSession.value = null
    }
}

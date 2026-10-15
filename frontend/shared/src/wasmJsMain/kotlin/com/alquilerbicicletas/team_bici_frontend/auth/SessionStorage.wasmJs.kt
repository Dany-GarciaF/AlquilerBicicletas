@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.alquilerbicicletas.team_bici_frontend.auth

import kotlinx.serialization.json.Json

actual class SessionStorage {
    actual fun read(): AuthSession? =
        getStoredValue(SESSION_KEY)?.let { encoded ->
            json.decodeFromString<AuthSession>(encoded)
        }

    actual fun write(session: AuthSession) {
        setStoredValue(SESSION_KEY, json.encodeToString(session))
    }

    actual fun clear() {
        removeStoredValue(SESSION_KEY)
    }

    private companion object {
        const val SESSION_KEY = "team-bici.auth-session"
        val json = Json { ignoreUnknownKeys = true }
    }
}

private fun getStoredValue(key: String): String? = js("window.sessionStorage.getItem(key)")

private fun setStoredValue(key: String, value: String) {
    js("window.sessionStorage.setItem(key, value)")
}

private fun removeStoredValue(key: String) {
    js("window.sessionStorage.removeItem(key)")
}

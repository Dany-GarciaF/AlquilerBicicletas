package com.alquilerbicicletas.team_bici_frontend.auth.google

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Hand-off point between platform entry points (browser location, Android deep link) and the shared UI.
 * Each callback is delivered once: consumers must call [consume] before processing it.
 */
class OAuthCallbackInbox {
    private val mutablePending = MutableStateFlow<OAuthCallback?>(null)

    val pending: StateFlow<OAuthCallback?> = mutablePending.asStateFlow()

    /** Returns true when [url] was a sign-in callback and has been queued. */
    fun publish(url: String): Boolean {
        val callback = OAuthCallbackParser.parse(url) ?: return false
        mutablePending.value = callback
        return true
    }

    fun consume(callback: OAuthCallback): Boolean = mutablePending.compareAndSet(callback, null)
}

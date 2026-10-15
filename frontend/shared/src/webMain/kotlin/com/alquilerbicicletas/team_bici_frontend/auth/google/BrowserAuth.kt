package com.alquilerbicicletas.team_bici_frontend.auth.google

import kotlinx.browser.window

/** Inicia el login con Google en la web redirigiendo la pestaña actual. */
private class BrowserRedirectLauncher : ExternalAuthLauncher {
    override val isMobileClient: Boolean = false

    override fun open(url: String) {
        window.location.assign(url)
    }
}

actual fun defaultExternalAuthLauncher(): ExternalAuthLauncher? = BrowserRedirectLauncher()

/** Queues the sign-in callback present in the current URL (if any) and removes it from the address bar. */
fun OAuthCallbackInbox.publishBrowserLocation() {
    if (publish(window.location.href)) {
        window.history.replaceState(null, "", "/")
    }
}

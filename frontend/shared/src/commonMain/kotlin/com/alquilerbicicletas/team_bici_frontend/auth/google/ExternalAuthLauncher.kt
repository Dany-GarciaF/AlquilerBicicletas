package com.alquilerbicicletas.team_bici_frontend.auth.google

/** Opens the identity provider flow outside the app (same-tab redirect on web, browser on Android). */
interface ExternalAuthLauncher {
    /** True when the final redirect must target the app deep link instead of the web callback URL. */
    val isMobileClient: Boolean

    fun open(url: String)
}

/** Platform default launcher, or null when the platform requires one to be supplied by the host app. */
expect fun defaultExternalAuthLauncher(): ExternalAuthLauncher?

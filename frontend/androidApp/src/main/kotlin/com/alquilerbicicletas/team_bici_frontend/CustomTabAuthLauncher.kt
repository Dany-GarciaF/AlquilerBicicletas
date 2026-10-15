package com.alquilerbicicletas.team_bici_frontend

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.alquilerbicicletas.team_bici_frontend.auth.google.ExternalAuthLauncher

/** Opens the Google sign-in flow in a Custom Tab; the backend returns to the app through its deep link. */
class CustomTabAuthLauncher(private val context: Context) : ExternalAuthLauncher {
    override val isMobileClient: Boolean = true

    override fun open(url: String) {
        CustomTabsIntent.Builder().build().launchUrl(context, url.toUri())
    }
}
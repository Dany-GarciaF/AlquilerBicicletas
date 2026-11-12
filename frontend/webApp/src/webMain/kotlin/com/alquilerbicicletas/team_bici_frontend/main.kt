package com.alquilerbicicletas.team_bici_frontend

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.alquilerbicicletas.team_bici_frontend.auth.google.OAuthCallbackInbox
import com.alquilerbicicletas.team_bici_frontend.auth.google.publishBrowserLocation
import com.alquilerbicicletas.team_bici_frontend.config.ApiConfiguration

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    configuredApiUrl()?.takeIf { it.isNotBlank() }?.let(ApiConfiguration::configure)
    val callbackInbox = OAuthCallbackInbox().also { it.publishBrowserLocation() }
    ComposeViewport {
        App(callbackInbox = callbackInbox)
    }
}

private fun configuredApiUrl(): String? = js("window.TEAM_BICI_API_URL")

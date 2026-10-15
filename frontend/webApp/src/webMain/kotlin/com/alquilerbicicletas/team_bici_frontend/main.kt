package com.alquilerbicicletas.team_bici_frontend

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.alquilerbicicletas.team_bici_frontend.auth.google.OAuthCallbackInbox
import com.alquilerbicicletas.team_bici_frontend.auth.google.publishBrowserLocation

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val callbackInbox = OAuthCallbackInbox().also { it.publishBrowserLocation() }
    ComposeViewport {
        App(callbackInbox = callbackInbox)
    }
}
package com.alquilerbicicletas.team_bici_frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.alquilerbicicletas.team_bici_frontend.auth.google.OAuthCallbackInbox

/** Actividad única de Android: muestra la app y reenvía los deep links de Google al flujo de login. */
class MainActivity : ComponentActivity() {
    private val callbackInbox = OAuthCallbackInbox()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        publishCallback(intent)

        setContent {
            App(
                authLauncher = CustomTabAuthLauncher(this),
                callbackInbox = callbackInbox,
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        publishCallback(intent)
    }

    private fun publishCallback(intent: Intent?) {
        intent?.dataString?.let(callbackInbox::publish)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
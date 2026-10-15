package com.alquilerbicicletas.team_bici_frontend

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.alquilerbicicletas.team_bici_frontend.auth.SessionManager
import com.alquilerbicicletas.team_bici_frontend.auth.SessionStorage
import com.alquilerbicicletas.team_bici_frontend.auth.google.ExternalAuthLauncher
import com.alquilerbicicletas.team_bici_frontend.auth.google.GoogleSignInController
import com.alquilerbicicletas.team_bici_frontend.auth.google.OAuthCallbackInbox
import com.alquilerbicicletas.team_bici_frontend.auth.google.defaultExternalAuthLauncher
import com.alquilerbicicletas.team_bici_frontend.network.ApiHttpClient
import com.alquilerbicicletas.team_bici_frontend.network.AuthRepository
import com.alquilerbicicletas.team_bici_frontend.network.AlquilerRepository
import com.alquilerbicicletas.team_bici_frontend.network.BicicletaRepository
import com.alquilerbicicletas.team_bici_frontend.network.EstacionRepository
import com.alquilerbicicletas.team_bici_frontend.network.ReservaRepository
import com.alquilerbicicletas.team_bici_frontend.network.TarifaRepository
import com.alquilerbicicletas.team_bici_frontend.ui.MainScreen
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.TeamBiciTheme
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

/** Contenedor de dependencias de la app: sesión, cliente HTTP y repositorios. */
private class AppContainer(
    authLauncher: ExternalAuthLauncher?,
    callbackInbox: OAuthCallbackInbox,
) {
    val sessionManager = SessionManager(SessionStorage())
    val apiHttpClient = ApiHttpClient(sessionManager)
    val authRepository = AuthRepository(apiHttpClient, sessionManager)
    val bicicletaRepository = BicicletaRepository(apiHttpClient)
    val estacionRepository = EstacionRepository(apiHttpClient)
    val tarifaRepository = TarifaRepository(apiHttpClient)
    val reservaRepository = ReservaRepository(apiHttpClient)
    val alquilerRepository = AlquilerRepository(apiHttpClient)
    val googleSignIn = GoogleSignInController(authRepository, authLauncher, callbackInbox)

    fun close() = apiHttpClient.close()
}

@Composable
fun App(
    authLauncher: ExternalAuthLauncher? = defaultExternalAuthLauncher(),
    callbackInbox: OAuthCallbackInbox = remember { OAuthCallbackInbox() },
) {
    val container = remember { AppContainer(authLauncher, callbackInbox) }
    val session by container.sessionManager.session.collectAsState()

    LaunchedEffect(container) {
        container.googleSignIn.collectCallbacks()
    }

    DisposableEffect(container) {
        onDispose(container::close)
    }

    val systemDark = isSystemInDarkTheme()
    var darkOverride by remember { mutableStateOf<Boolean?>(null) }
    val darkTheme = darkOverride ?: systemDark
    val toggleTheme = { darkOverride = !darkTheme }

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context).components { add(KtorNetworkFetcherFactory()) }.build()
    }

    TeamBiciTheme(darkTheme = darkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            CompositionLocalProvider(LocalWindowSize provides WindowSize.fromWidth(maxWidth)) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(
                        session = session,
                        authRepository = container.authRepository,
                        googleSignIn = container.googleSignIn,
                        bicicletaRepository = container.bicicletaRepository,
                        estacionRepository = container.estacionRepository,
                        tarifaRepository = container.tarifaRepository,
                        reservaRepository = container.reservaRepository,
                        alquilerRepository = container.alquilerRepository,
                        darkTheme = darkTheme,
                        onToggleTheme = toggleTheme,
                    )
                }
            }
        }
    }
}
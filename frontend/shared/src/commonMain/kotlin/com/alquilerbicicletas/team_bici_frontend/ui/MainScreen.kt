package com.alquilerbicicletas.team_bici_frontend.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.alquilerbicicletas.team_bici_frontend.auth.AuthSession
import com.alquilerbicicletas.team_bici_frontend.auth.google.GoogleSignInController
import com.alquilerbicicletas.team_bici_frontend.auth.google.GoogleSignInState
import com.alquilerbicicletas.team_bici_frontend.network.AlquilerRepository
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.AuthRepository
import com.alquilerbicicletas.team_bici_frontend.network.BicicletaRepository
import com.alquilerbicicletas.team_bici_frontend.network.EstacionRepository
import com.alquilerbicicletas.team_bici_frontend.network.ReservaRepository
import com.alquilerbicicletas.team_bici_frontend.network.TarifaRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.Rol
import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta
import com.alquilerbicicletas.team_bici_frontend.ui.administracion.AdministracionScreen
import com.alquilerbicicletas.team_bici_frontend.ui.alquileres.MisAlquileresScreen
import com.alquilerbicicletas.team_bici_frontend.ui.bicicletas.BicicletasScreen
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.BikeActionDialogs
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogFilters
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.PendingBikeIntent
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.rememberBikeActions
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.rememberCatalogState
import com.alquilerbicicletas.team_bici_frontend.ui.estaciones.EstacionesScreen
import com.alquilerbicicletas.team_bici_frontend.ui.home.HomeScreen
import com.alquilerbicicletas.team_bici_frontend.ui.navigation.AccountActions
import com.alquilerbicicletas.team_bici_frontend.ui.navigation.AppDestination
import com.alquilerbicicletas.team_bici_frontend.ui.navigation.AppScaffold
import com.alquilerbicicletas.team_bici_frontend.ui.navigation.AuthShell
import com.alquilerbicicletas.team_bici_frontend.ui.reservas.MisReservasScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val AuthRequiredNotice = "Inicia sesión o crea una cuenta para reservar o alquilar tu bicicleta."

/**
 * Root of the app for both visitors and signed-in users. Public destinations (home, catalog,
 * stations) are always reachable; signing in is requested only when the user tries to reserve or rent.
 */
@Composable
fun MainScreen(
    session: AuthSession?,
    authRepository: AuthRepository,
    googleSignIn: GoogleSignInController,
    bicicletaRepository: BicicletaRepository,
    estacionRepository: EstacionRepository,
    tarifaRepository: TarifaRepository,
    reservaRepository: ReservaRepository,
    alquilerRepository: AlquilerRepository,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val signedIn = session != null
    val isAdministrator = session?.usuario?.rol == Rol.ADMINISTRADOR

    var destination by remember { mutableStateOf(AppDestination.INICIO) }
    var authMode by remember { mutableStateOf<AuthMode?>(null) }
    var pending by remember { mutableStateOf<PendingBikeIntent?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var messageIsError by remember { mutableStateOf(false) }

    val catalog = rememberCatalogState(bicicletaRepository, estacionRepository, tarifaRepository)
    val filters = remember { CatalogFilters() }
    val homeListState = rememberLazyListState()
    val actions = rememberBikeActions(
        reservaRepository = reservaRepository,
        alquilerRepository = alquilerRepository,
        catalog = catalog,
        signedIn = signedIn,
        requireAuth = {
            pending = it
            authMode = AuthMode.LOGIN
        },
        onSuccess = {
            message = it
            messageIsError = false
        },
    )

    fun navigate(target: AppDestination) {
        if (destination == AppDestination.ADMINISTRACION && target != destination) {
            scope.launch { catalog.load() }
        }
        message = null
        destination = target
    }

    fun showBikes(estacionId: Long? = null, tipo: TipoBicicleta? = null) {
        filters.estacionId = estacionId
        filters.tipo = tipo
        navigate(AppDestination.BICICLETAS)
    }

    LaunchedEffect(signedIn) {
        if (signedIn) {
            authMode = null
            pending?.let {
                pending = null
                actions.resume(it)
            }
        } else if (!destination.isAvailableFor(signedIn = false, isAdministrator = false)) {
            destination = AppDestination.INICIO
        }
    }

    val googleState by googleSignIn.state.collectAsState()
    LaunchedEffect(googleState) {
        if (googleState !is GoogleSignInState.Idle && !signedIn && authMode == null) authMode = AuthMode.LOGIN
    }

    LaunchedEffect(message) {
        if (message != null) {
            delay(5_000)
            message = null
        }
    }

    val activeAuthMode = authMode
    if (!signedIn && activeAuthMode != null) {
        AuthShell(darkTheme = darkTheme, onToggleTheme = onToggleTheme) {
            AuthScreen(
                authRepository = authRepository,
                googleSignIn = googleSignIn,
                initialMode = activeAuthMode,
                notice = if (pending != null) AuthRequiredNotice else null,
                onBack = {
                    authMode = null
                    pending = null
                },
            )
        }
        return
    }

    AppScaffold(
        account = AccountActions(
            userName = session?.usuario?.nombre,
            onLogin = { authMode = AuthMode.LOGIN },
            onRegister = { authMode = AuthMode.REGISTRO },
            onLogout = {
                scope.launch {
                    try {
                        authRepository.logout()
                    } catch (exception: ApiRequestException) {
                        message = exception.message
                        messageIsError = true
                    }
                }
            },
        ),
        destinations = AppDestination.availableFor(signedIn, isAdministrator),
        selected = destination,
        onSelect = ::navigate,
        darkTheme = darkTheme,
        onToggleTheme = onToggleTheme,
        message = message,
        messageIsError = messageIsError,
    ) {
        when (destination) {
            AppDestination.INICIO -> HomeScreen(
                catalog = catalog,
                actions = actions,
                signedIn = signedIn,
                listState = homeListState,
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onSeeBikes = { showBikes() },
                onSeeStations = { navigate(AppDestination.ESTACIONES) },
                onSelectTipo = { showBikes(tipo = it) },
                onLogin = { authMode = AuthMode.LOGIN },
                onRegister = { authMode = AuthMode.REGISTRO },
            )

            AppDestination.BICICLETAS -> BicicletasScreen(catalog, filters, actions)
            AppDestination.ESTACIONES -> EstacionesScreen(catalog) { showBikes(estacionId = it.id) }
            AppDestination.RESERVAS -> MisReservasScreen(reservaRepository)
            AppDestination.ALQUILERES -> MisAlquileresScreen(alquilerRepository, estacionRepository)
            AppDestination.ADMINISTRACION -> AdministracionScreen(
                estacionRepository = estacionRepository,
                tarifaRepository = tarifaRepository,
                bicicletaRepository = bicicletaRepository,
            )
        }
        BikeActionDialogs(actions)
    }
}

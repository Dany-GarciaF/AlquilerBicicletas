package com.alquilerbicicletas.team_bici_frontend.auth.google

import com.alquilerbicicletas.team_bici_frontend.config.ApiConfiguration
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.AuthRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.OAuthCodeExchangeRequestDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull

/** Estados del inicio de sesión con Google que la interfaz muestra al usuario. */
sealed interface GoogleSignInState {
    data object Idle : GoogleSignInState

    data object Exchanging : GoogleSignInState

    data class Failed(val message: String) : GoogleSignInState
}

/** Orquesta el login con Google: abre el navegador, recibe el callback y canjea el código por una sesión. */
class GoogleSignInController(
    private val exchangeCode: suspend (OAuthCodeExchangeRequestDto) -> Unit,
    private val launcher: ExternalAuthLauncher?,
    private val inbox: OAuthCallbackInbox,
    private val apiBaseUrl: () -> String = { ApiConfiguration.baseUrl },
) {
    constructor(
        authRepository: AuthRepository,
        launcher: ExternalAuthLauncher?,
        inbox: OAuthCallbackInbox,
    ) : this({ authRepository.exchangeGoogleCode(it) }, launcher, inbox)

    private val mutableState = MutableStateFlow<GoogleSignInState>(
        if (inbox.pending.value != null) GoogleSignInState.Exchanging else GoogleSignInState.Idle,
    )

    val state: StateFlow<GoogleSignInState> = mutableState.asStateFlow()

    val isAvailable: Boolean get() = launcher != null

    fun start() {
        val activeLauncher = launcher ?: return
        mutableState.value = GoogleSignInState.Idle
        activeLauncher.open(authorizationUrl(activeLauncher.isMobileClient))
    }

    /** Processes callbacks as they arrive; runs until the calling coroutine is cancelled. */
    suspend fun collectCallbacks() {
        inbox.pending.filterNotNull().collect { callback ->
            if (inbox.consume(callback)) handle(callback)
        }
    }

    private suspend fun handle(callback: OAuthCallback) {
        when (callback) {
            is OAuthCallback.Failure -> mutableState.value = GoogleSignInState.Failed(messageFor(callback.reason))
            is OAuthCallback.Success -> exchange(callback.code)
        }
    }

    private suspend fun exchange(code: String) {
        mutableState.value = GoogleSignInState.Exchanging
        mutableState.value = try {
            exchangeCode(OAuthCodeExchangeRequestDto(code))
            GoogleSignInState.Idle
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: ApiRequestException) {
            GoogleSignInState.Failed(exception.message)
        } catch (_: Exception) {
            GoogleSignInState.Failed("No fue posible conectar con el servidor. Intenta nuevamente.")
        }
    }

    private fun authorizationUrl(mobile: Boolean): String {
        val base = "${apiBaseUrl().trimEnd('/')}/oauth2/authorization/google"
        return if (mobile) "$base?client=mobile" else base
    }

    private fun messageFor(reason: String): String = when (reason) {
        "google_auth_failed" -> "No se pudo iniciar sesión con Google. Verifica que tu correo esté confirmado e intenta nuevamente."
        else -> "El inicio de sesión con Google no se completó."
    }
}

package com.alquilerbicicletas.team_bici_frontend.ui.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.alquilerbicicletas.team_bici_frontend.network.AlquilerRepository
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.ReservaRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.AlquilerRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.ReservaRequestDto
import com.alquilerbicicletas.team_bici_frontend.ui.bicicletas.AlquilerDialog
import com.alquilerbicicletas.team_bici_frontend.ui.bicicletas.ReservaDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Acción que el visitante quiso hacer sobre una bicicleta antes de iniciar sesión. */
enum class BikeIntent { RESERVAR, ALQUILAR }

/** An action the visitor wanted to perform before being asked to sign in. */
data class PendingBikeIntent(val bicicletaId: Long, val intent: BikeIntent)

/**
 * Reserve / rent flow shared by every screen that lists bikes. Visitors without a session are
 * redirected to sign in through [requireAuth]; the intent is resumed afterwards with [resume].
 */
class BikeActions(
    private val scope: CoroutineScope,
    private val reservaRepository: ReservaRepository,
    private val alquilerRepository: AlquilerRepository,
    private val catalog: CatalogState,
    private val requireAuth: (PendingBikeIntent) -> Unit,
    private val onSuccess: (String) -> Unit,
) {
    var signedIn by mutableStateOf(false)
    var reserva by mutableStateOf<BicicletaResponseDto?>(null)
        private set
    var alquiler by mutableStateOf<BicicletaResponseDto?>(null)
        private set
    var submitting by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun reserve(bicicleta: BicicletaResponseDto) = open(bicicleta, BikeIntent.RESERVAR)

    fun rent(bicicleta: BicicletaResponseDto) = open(bicicleta, BikeIntent.ALQUILAR)

    fun resume(pending: PendingBikeIntent) {
        val bicicleta = catalog.bicicletas.firstOrNull { it.id == pending.bicicletaId } ?: return
        open(bicicleta, pending.intent)
    }

    fun dismiss() {
        if (submitting) return
        reserva = null
        alquiler = null
        error = null
    }

    fun confirmReserva(inicio: String, fin: String) {
        val bicicleta = reserva ?: return
        submit {
            reservaRepository.create(
                ReservaRequestDto(
                    bicicletaId = bicicleta.id,
                    estacionRetiroId = bicicleta.estacionId,
                    fechaInicio = inicio,
                    fechaFin = fin,
                ),
            )
            reserva = null
            onSuccess("La reserva fue confirmada")
        }
    }

    fun confirmAlquiler() {
        val bicicleta = alquiler ?: return
        submit {
            alquilerRepository.create(
                AlquilerRequestDto(bicicletaId = bicicleta.id, estacionRetiroId = bicicleta.estacionId),
            )
            alquiler = null
            onSuccess("El alquiler está activo. Disfruta tu recorrido.")
        }
    }

    private fun open(bicicleta: BicicletaResponseDto, intent: BikeIntent) {
        if (!signedIn) {
            requireAuth(PendingBikeIntent(bicicleta.id, intent))
            return
        }
        error = null
        when (intent) {
            BikeIntent.RESERVAR -> reserva = bicicleta
            BikeIntent.ALQUILAR -> alquiler = bicicleta
        }
    }

    private fun submit(action: suspend () -> Unit) {
        scope.launch {
            submitting = true
            error = null
            try {
                action()
                catalog.load()
            } catch (exception: ApiRequestException) {
                error = exception.message
            } finally {
                submitting = false
            }
        }
    }
}

@Composable
fun rememberBikeActions(
    reservaRepository: ReservaRepository,
    alquilerRepository: AlquilerRepository,
    catalog: CatalogState,
    signedIn: Boolean,
    requireAuth: (PendingBikeIntent) -> Unit,
    onSuccess: (String) -> Unit,
): BikeActions {
    val scope = rememberCoroutineScope()
    val actions = remember(catalog) {
        BikeActions(scope, reservaRepository, alquilerRepository, catalog, requireAuth, onSuccess)
    }
    actions.signedIn = signedIn
    return actions
}

@Composable
fun BikeActionDialogs(actions: BikeActions) {
    actions.reserva?.let { bicicleta ->
        ReservaDialog(
            bicicleta = bicicleta,
            submitting = actions.submitting,
            submitError = actions.error,
            onDismiss = actions::dismiss,
            onConfirm = actions::confirmReserva,
        )
    }
    actions.alquiler?.let { bicicleta ->
        AlquilerDialog(
            bicicleta = bicicleta,
            submitting = actions.submitting,
            submitError = actions.error,
            onDismiss = actions::dismiss,
            onConfirm = actions::confirmAlquiler,
        )
    }
}

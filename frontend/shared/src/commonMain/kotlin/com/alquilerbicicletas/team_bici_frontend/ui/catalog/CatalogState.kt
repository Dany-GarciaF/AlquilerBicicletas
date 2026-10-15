package com.alquilerbicicletas.team_bici_frontend.ui.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.BicicletaRepository
import com.alquilerbicicletas.team_bici_frontend.network.EstacionRepository
import com.alquilerbicicletas.team_bici_frontend.network.TarifaRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.TarifaResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta

/** Public catalog (stations, available bikes, rates) shared by the home, catalog and stations screens. */
class CatalogState(
    private val bicicletaRepository: BicicletaRepository,
    private val estacionRepository: EstacionRepository,
    private val tarifaRepository: TarifaRepository,
) {
    var estaciones by mutableStateOf<List<EstacionResponseDto>>(emptyList())
        private set
    var bicicletas by mutableStateOf<List<BicicletaResponseDto>>(emptyList())
        private set
    var tarifas by mutableStateOf<List<TarifaResponseDto>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    /** True while there is nothing to show yet and the request is in flight. */
    val showSkeleton: Boolean get() = loading && bicicletas.isEmpty() && estaciones.isEmpty()

    /** True when the load failed and there is no data to fall back on. */
    val loadFailed: Boolean get() = error != null && !loading && bicicletas.isEmpty() && estaciones.isEmpty()

    suspend fun load() {
        loading = true
        error = null
        try {
            estaciones = estacionRepository.list().filter { it.activa }
            bicicletas = bicicletaRepository.listAvailable()
            tarifas = tarifaRepository.list()
        } catch (exception: ApiRequestException) {
            error = exception.message
        } finally {
            loading = false
        }
    }

    fun tarifaFor(tipo: TipoBicicleta): TarifaResponseDto? =
        tarifas.firstOrNull { it.activa && it.tipoBicicleta == tipo }

    fun availableAt(estacionId: Long): Int = bicicletas.count { it.estacionId == estacionId }

    fun availableOfType(tipo: TipoBicicleta): Int = bicicletas.count { it.tipo == tipo }

    fun lowestRate(): Double? = tarifas.filter { it.activa }.minOfOrNull { it.valorHora }
}

/** Active filters of the catalog screen; set from the home categories or the stations list. */
class CatalogFilters {
    var estacionId by mutableStateOf<Long?>(null)
    var tipo by mutableStateOf<TipoBicicleta?>(null)

    fun apply(bicicletas: List<BicicletaResponseDto>): List<BicicletaResponseDto> =
        bicicletas.filter { (estacionId == null || it.estacionId == estacionId) && (tipo == null || it.tipo == tipo) }
}

@Composable
fun rememberCatalogState(
    bicicletaRepository: BicicletaRepository,
    estacionRepository: EstacionRepository,
    tarifaRepository: TarifaRepository,
): CatalogState {
    val state = remember { CatalogState(bicicletaRepository, estacionRepository, tarifaRepository) }
    LaunchedEffect(state) { state.load() }
    return state
}

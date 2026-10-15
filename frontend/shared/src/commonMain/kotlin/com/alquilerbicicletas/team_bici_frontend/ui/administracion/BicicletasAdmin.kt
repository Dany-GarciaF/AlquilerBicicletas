package com.alquilerbicicletas.team_bici_frontend.ui.administracion

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.BicicletaRepository
import com.alquilerbicicletas.team_bici_frontend.network.EstacionRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstacionResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstadoBicicleta
import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikeIllustration
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikePhoto
import com.alquilerbicicletas.team_bici_frontend.ui.components.ImagePickerField
import com.alquilerbicicletas.team_bici_frontend.ui.components.PickedImage
import com.alquilerbicicletas.team_bici_frontend.ui.components.ChipRow
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormDialog
import com.alquilerbicicletas.team_bici_frontend.ui.components.FormTextField
import com.alquilerbicicletas.team_bici_frontend.ui.components.StatusBadge
import com.alquilerbicicletas.team_bici_frontend.ui.estadoLabel
import kotlinx.coroutines.launch

@Composable
fun BicicletasAdmin(
    repository: BicicletaRepository,
    estacionRepository: EstacionRepository,
    header: @Composable () -> Unit,
) {
    var entries by remember { mutableStateOf<List<BicicletaResponseDto>>(emptyList()) }
    var stations by remember { mutableStateOf<List<EstacionResponseDto>>(emptyList()) }
    var editing by remember { mutableStateOf<BicicletaResponseDto?>(null) }
    var creating by remember { mutableStateOf(false) }
    var changingStatus by remember { mutableStateOf<BicicletaResponseDto?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    suspend fun reload() {
        loading = true
        error = null
        try {
            entries = repository.list()
            stations = estacionRepository.list().filter { it.activa }
        } catch (exception: ApiRequestException) {
            error = exception.message
        } finally {
            loading = false
        }
    }
    LaunchedEffect(Unit) { reload() }

    if (creating || editing != null) {
        BicicletaForm(editing, stations, { creating = false; editing = null }) { request, image, removeImage ->
            scope.launch {
                try {
                    val saved = if (editing == null) repository.create(request) else repository.update(editing!!.id, request)
                    when {
                        image != null -> repository.uploadImage(saved.id, image.bytes, image.fileName, image.contentType)
                        removeImage && saved.imagenUrl != null -> repository.deleteImage(saved.id)
                    }
                    creating = false
                    editing = null
                    reload()
                } catch (exception: ApiRequestException) {
                    error = exception.message
                }
            }
        }
    }
    changingStatus?.let { bike ->
        EstadoBicicletaDialog(bike, { changingStatus = null }) { status ->
            scope.launch {
                try {
                    repository.changeStatus(bike.id, status)
                    changingStatus = null
                    reload()
                } catch (exception: ApiRequestException) {
                    error = exception.message
                }
            }
        }
    }

    AdminSectionLayout(
        header = header,
        title = "Bicicletas",
        actionLabel = "Nueva bicicleta",
        loading = loading,
        error = error,
        isEmpty = entries.isEmpty(),
        emptyMessage = "Registra la primera bicicleta de la flota.",
        onCreate = { creating = true },
    ) {
        items(entries, key = { it.id }) { entry ->
            AppCard(Modifier.fillMaxWidth().animateContentSize()) {
                Box(Modifier.fillMaxWidth().height(150.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
                    BikePhoto(
                        url = entry.imagenUrl,
                        contentDescription = "${entry.marca} ${entry.modelo}",
                        modifier = Modifier.fillMaxSize(),
                        fallback = {
                            BikeIllustration(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                Modifier.fillMaxSize().padding(16.dp),
                            )
                        },
                    )
                }
                AdminCardBody {
                    Text("${entry.marca} ${entry.modelo}", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${entry.codigo} · ${estadoLabel(entry.tipo.name)} · ${entry.estacionNombre}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    StatusBadge(estadoLabel(entry.estado.name), entry.estado.tone())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { editing = entry }) { Text("Editar") }
                        Button(onClick = { changingStatus = entry }) {
                            Text(if (entry.estado == EstadoBicicleta.MANTENIMIENTO) "Gestionar estado" else "Cambiar estado")
                        }
                    }
                }
            }
        }
    }
}

private fun EstadoBicicleta.tone(): BadgeTone = when (this) {
    EstadoBicicleta.DISPONIBLE -> BadgeTone.SUCCESS
    EstadoBicicleta.RESERVADA -> BadgeTone.WARNING
    EstadoBicicleta.ALQUILADA -> BadgeTone.ACCENT
    EstadoBicicleta.MANTENIMIENTO -> BadgeTone.DANGER
}

@Composable
private fun BicicletaForm(
    initial: BicicletaResponseDto?,
    stations: List<EstacionResponseDto>,
    onDismiss: () -> Unit,
    onSave: (BicicletaRequestDto, PickedImage?, Boolean) -> Unit,
) {
    var code by remember(initial) { mutableStateOf(initial?.codigo.orEmpty()) }
    var brand by remember(initial) { mutableStateOf(initial?.marca.orEmpty()) }
    var model by remember(initial) { mutableStateOf(initial?.modelo.orEmpty()) }
    var type by remember(initial) { mutableStateOf(initial?.tipo ?: TipoBicicleta.URBANA) }
    var stationId by remember(initial, stations) { mutableStateOf(initial?.estacionId ?: stations.firstOrNull()?.id) }
    var submitted by remember(initial) { mutableStateOf(false) }
    var image by remember(initial) { mutableStateOf<PickedImage?>(null) }
    var imageRemoved by remember(initial) { mutableStateOf(false) }
    val required = "Campo obligatorio"

    FormDialog(
        title = if (initial == null) "Nueva bicicleta" else "Editar bicicleta",
        onDismiss = onDismiss,
        onConfirm = {
            submitted = true
            stationId?.let { id ->
                if (code.isNotBlank() && brand.isNotBlank() && model.isNotBlank()) {
                    onSave(BicicletaRequestDto(code.trim(), brand.trim(), model.trim(), type, id), image, imageRemoved && image == null)
                }
            }
        },
    ) {
        FormTextField("Código", code, required.takeIf { submitted && code.isBlank() }) { code = it }
        FormTextField("Marca", brand, required.takeIf { submitted && brand.isBlank() }) { brand = it }
        FormTextField("Modelo", model, required.takeIf { submitted && model.isBlank() }) { model = it }
        ImagePickerField(
            currentUrl = initial?.imagenUrl,
            picked = image,
            removed = imageRemoved,
            onPicked = { image = it; imageRemoved = false },
            onRemove = { image = null; imageRemoved = true },
        )
        Text("Tipo")
        ChipRow {
            TipoBicicleta.entries.forEach {
                FilterChip(type == it, { type = it }, { Text(estadoLabel(it.name)) })
            }
        }
        Text("Estación")
        if (stations.isEmpty()) {
            Text("Primero crea una estación activa.", color = MaterialTheme.colorScheme.error)
        }
        ChipRow {
            stations.forEach { station ->
                FilterChip(stationId == station.id, { stationId = station.id }, { Text(station.nombre) })
            }
        }
    }
}

@Composable
private fun EstadoBicicletaDialog(bike: BicicletaResponseDto, onDismiss: () -> Unit, onChange: (EstadoBicicleta) -> Unit) {
    var selected by remember(bike) { mutableStateOf(bike.estado) }
    FormDialog(
        title = "Estado de ${bike.codigo}",
        onDismiss = onDismiss,
        onConfirm = { onChange(selected) },
        confirmLabel = "Guardar estado",
    ) {
        Text("Selecciona el estado operativo. Enviar a mantenimiento bloquea la disponibilidad de la bicicleta.")
        ChipRow {
            EstadoBicicleta.entries.forEach { state ->
                FilterChip(selected == state, { selected = state }, { Text(estadoLabel(state.name)) })
            }
        }
    }
}

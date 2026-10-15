package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

/** Image chosen by the administrator, waiting to be uploaded. */
class PickedImage(val bytes: ByteArray, val fileName: String, val contentType: String)

private const val MaxImageBytes = 5 * 1024 * 1024

private fun contentTypeOf(fileName: String): String? = when (fileName.substringAfterLast('.', "").lowercase()) {
    "jpg", "jpeg" -> "image/jpeg"
    "png" -> "image/png"
    "webp" -> "image/webp"
    else -> null
}

/** Preview plus pick/remove actions for a photo (the saved one or a freshly picked file). */
@Composable
fun ImagePickerField(
    currentUrl: String?,
    picked: PickedImage?,
    removed: Boolean,
    onPicked: (PickedImage) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }
    val launcher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file == null) return@rememberFilePickerLauncher
        scope.launch {
            val type = contentTypeOf(file.name)
            if (type == null) {
                error = "Usa una imagen JPG, PNG o WebP"
                return@launch
            }
            val bytes = file.readBytes()
            if (bytes.size > MaxImageBytes) {
                error = "La imagen no puede superar 5 MB"
                return@launch
            }
            error = null
            onPicked(PickedImage(bytes, file.name, type))
        }
    }
    val hasImage = picked != null || (currentUrl != null && !removed)

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Fotografía", style = MaterialTheme.typography.labelLarge)
        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            when {
                picked != null -> AsyncImage(picked.bytes, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                currentUrl != null && !removed -> BikePhoto(currentUrl, Modifier.fillMaxSize())
                else -> Box(Modifier.fillMaxSize().padding(16.dp)) {
                    BikeIllustration(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), Modifier.fillMaxSize())
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { launcher.launch() }) { Text(if (hasImage) "Cambiar imagen" else "Subir imagen") }
            if (hasImage) TextButton(onClick = onRemove) { Text("Quitar") }
        }
        Text(
            error ?: "JPG, PNG o WebP de hasta 5 MB.",
            style = MaterialTheme.typography.bodySmall,
            color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
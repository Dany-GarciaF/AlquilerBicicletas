package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import team_bici_frontend.shared.generated.resources.Res

/**
 * Brand images bundled with the app under `composeResources/files`. Each image is looked up by name
 * with a .jpg, .png or .webp extension; a missing image simply yields null so screens can fall back.
 */
object StaticImages {
    private val extensions = listOf("jpg", "png", "webp")
    private val cache = mutableMapOf<String, ByteArray?>()

    suspend fun load(name: String): ByteArray? {
        if (cache.containsKey(name)) return cache[name]
        val bytes = extensions.firstNotNullOfOrNull { extension ->
            runCatching { Res.readBytes("files/$name.$extension") }.getOrNull()
        }
        cache[name] = bytes
        return bytes
    }

    /** Loads `prefix-1`, `prefix-2`, ... until the first one that does not exist. */
    suspend fun loadSequence(prefix: String, max: Int = 5): List<ByteArray> {
        val images = mutableListOf<ByteArray>()
        for (index in 1..max) images += load("$prefix-$index") ?: break
        return images
    }
}

@Composable
fun rememberStaticImage(name: String): State<ByteArray?> =
    produceState<ByteArray?>(null, name) { value = StaticImages.load(name) }

@Composable
fun rememberStaticImages(prefix: String): State<List<ByteArray>> =
    produceState(emptyList(), prefix) { value = StaticImages.loadSequence(prefix) }

@Composable
fun StaticPhoto(
    bytes: ByteArray,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null,
) {
    val request = ImageRequest.Builder(LocalPlatformContext.current).data(bytes).crossfade(400).build()
    AsyncImage(
        model = request,
        contentDescription = null,
        contentScale = contentScale,
        alignment = alignment,
        colorFilter = colorFilter,
        modifier = modifier,
    )
}
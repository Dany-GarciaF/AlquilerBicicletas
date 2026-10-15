package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alquilerbicicletas.team_bici_frontend.config.ApiConfiguration

/**
 * Remote bicycle photo that crossfades in. [fallback] is shown when there is no [url] and while the
 * image loads or fails, so the layout never jumps.
 */
@Composable
fun BikePhoto(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    fallback: @Composable () -> Unit = {},
) {
    if (url == null) {
        Box(modifier) { fallback() }
        return
    }
    val request = ImageRequest.Builder(LocalPlatformContext.current)
        .data(ApiConfiguration.mediaUrl(url))
        .crossfade(400)
        .build()
    SubcomposeAsyncImage(
        model = request,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = { fallback() },
        error = { fallback() },
    )
}
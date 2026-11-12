package com.alquilerbicicletas.team_bici_frontend.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.network.dto.AjusteImagen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.alquilerbicicletas.team_bici_frontend.config.ApiConfiguration

/**
 * Remote bicycle photo that crossfades in; [ajuste] chooses between cropping to fill or showing it whole. [fallback] is shown when there is no [url] and while the
 * image loads or fails, so the layout never jumps.
 */
@Composable
fun BikePhoto(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    ajuste: AjusteImagen = AjusteImagen.RELLENAR,
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
    if (ajuste == AjusteImagen.COMPLETA) {
        // Whole photo centered, over a blurred copy of itself so there are no empty bars.
        Box(modifier) {
            SubcomposeAsyncImage(
                model = request,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(24.dp).alpha(0.65f),
                loading = { fallback() },
                error = { fallback() },
            )
            SubcomposeAsyncImage(
                model = request,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
                loading = {},
                error = {},
            )
        }
        return
    }
    SubcomposeAsyncImage(
        model = request,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = { fallback() },
        error = { fallback() },
    )
}
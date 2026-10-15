package com.alquilerbicicletas.team_bici_frontend.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Clase de tamaño de pantalla (compacta, media, expandida) y su margen de página, para el diseño responsivo. */
enum class WindowSize(val pagePadding: Dp) {
    COMPACT(16.dp),
    MEDIUM(24.dp),
    EXPANDED(32.dp),
    ;

    val usesBottomNavigation: Boolean get() = this == COMPACT

    companion object {
        fun fromWidth(width: Dp): WindowSize = when {
            width < 900.dp -> COMPACT
            width < 1200.dp -> MEDIUM
            else -> EXPANDED
        }
    }
}

val LocalWindowSize = compositionLocalOf { WindowSize.EXPANDED }

val MaxContentWidth = 1200.dp

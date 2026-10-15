package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogState
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikeIllustration
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikePhoto
import com.alquilerbicicletas.team_bici_frontend.ui.components.StaticPhoto
import com.alquilerbicicletas.team_bici_frontend.ui.components.rememberStaticImage
import androidx.compose.runtime.getValue
import com.alquilerbicicletas.team_bici_frontend.ui.components.hoverScale
import com.alquilerbicicletas.team_bici_frontend.ui.formatCurrency
import com.alquilerbicicletas.team_bici_frontend.ui.theme.BrandGradient
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.tipoLabel

/** Fondo, color de texto y descripción de la casilla de cada tipo de bicicleta. */
private data class CategoryStyle(
    val brush: Brush,
    val content: Color,
    val description: String,
)

private val InkTile = Brush.linearGradient(listOf(Color(0xFF1B1D22), Color(0xFF30333B)))
private val AmberTile = Brush.linearGradient(listOf(Color(0xFFB8721E), Color(0xFFE0993A)))

@Composable
private fun styleFor(tipo: TipoBicicleta): CategoryStyle = when (tipo) {
    TipoBicicleta.URBANA -> CategoryStyle(BrandGradient, Color.White, "Cómoda y ágil para moverte por la ciudad.")
    TipoBicicleta.MONTANA -> CategoryStyle(InkTile, Color.White, "Resistencia y tracción para rutas exigentes.")
    TipoBicicleta.ELECTRICA -> CategoryStyle(
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)),
        MaterialTheme.colorScheme.onSurface,
        "Pedalea con asistencia y llega sin esfuerzo.",
    )
    TipoBicicleta.PLEGABLE -> CategoryStyle(AmberTile, Color.White, "Compacta y práctica para combinar con otros trayectos.")
}

@Composable
fun CategoriesSection(catalog: CatalogState, onSelect: (TipoBicicleta) -> Unit) {
    val columns = if (LocalWindowSize.current == WindowSize.COMPACT) 1 else 2
    SectionContainer(verticalPadding = 32.dp) {
        SectionTitle(
            title = "Elige tu estilo de ruta",
            subtitle = "Cuatro tipos de bicicleta para cada trayecto, con tarifas claras por hora.",
        )
        Column(Modifier.padding(top = 24.dp)) {
            ResponsiveRows(items = TipoBicicleta.entries.toList(), columns = columns) { tipo ->
                CategoryTile(tipo = tipo, catalog = catalog, onClick = { onSelect(tipo) })
            }
        }
    }
}

@Composable
private fun CategoryTile(tipo: TipoBicicleta, catalog: CatalogState, onClick: () -> Unit) {
    // Transparent bike cutout bundled with the app; tinted with the tile's text color.
    val cutout by rememberStaticImage("categoria-${tipo.name.lowercase()}")
    val remotePhoto = if (cutout == null) catalog.bicicletas.firstOrNull { it.tipo == tipo && it.imagenUrl != null }?.imagenUrl else null
    val style = styleFor(tipo).let { if (remotePhoto != null) it.copy(content = Color.White) else it }
    val available = catalog.availableOfType(tipo)
    val rate = catalog.tarifaFor(tipo)?.valorHora
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .hoverScale(1.02f)
            .clip(RoundedCornerShape(24.dp))
            .background(style.brush)
            .clickable(onClick = onClick),
    ) {
        val cutoutBytes = cutout
        if (cutoutBytes != null) {
            StaticPhoto(
                bytes = cutoutBytes,
                modifier = Modifier.align(Alignment.BottomEnd).fillMaxHeight(0.78f).fillMaxWidth(0.58f).padding(end = 16.dp, bottom = 14.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.BottomEnd,
                colorFilter = ColorFilter.tint(style.content.copy(alpha = 0.92f)),
            )
        } else if (remotePhoto != null) {
            BikePhoto(remotePhoto, Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.72f), Color.Transparent))))
        } else {
            BikeIllustration(
                color = style.content.copy(alpha = 0.18f),
                strokeWidth = 4.dp,
                modifier = Modifier.align(Alignment.BottomEnd).fillMaxHeight(0.8f).widthIn(max = 260.dp).fillMaxWidth(0.6f),
            )
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(tipoLabel(tipo).uppercase(), color = style.content, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(
                style.description,
                color = style.content.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(if (cutout != null) 0.5f else 0.62f),
            )
            Box(Modifier.weight(1f))
            Text(
                buildString {
                    append("$available disponibles")
                    rate?.let { append(" · desde ${formatCurrency(it)}/h") }
                },
                color = style.content,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .background(style.content.copy(alpha = 0.16f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}

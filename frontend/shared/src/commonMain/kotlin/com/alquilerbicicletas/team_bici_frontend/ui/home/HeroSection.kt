package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.background
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.alquilerbicicletas.team_bici_frontend.ui.components.StaticPhoto
import com.alquilerbicicletas.team_bici_frontend.ui.components.rememberStaticImages
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikeIllustration
import com.alquilerbicicletas.team_bici_frontend.ui.formatCurrency
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

@Composable
fun HeroSection(
    availableBikes: Int,
    stations: Int,
    lowestRate: Double?,
    signedIn: Boolean,
    onSeeBikes: () -> Unit,
    onHowItWorks: () -> Unit,
    onRegister: () -> Unit,
) {
    val windowSize = LocalWindowSize.current
    val wide = windowSize != WindowSize.COMPACT
    val titleSize = when (windowSize) {
        WindowSize.COMPACT -> 40.sp
        WindowSize.MEDIUM -> 54.sp
        WindowSize.EXPANDED -> 68.sp
    }

    SectionContainer(verticalPadding = if (wide) 64.dp else 32.dp) {
        if (wide) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
                HeroCopy(
                    titleSize, availableBikes, stations, lowestRate, signedIn,
                    onSeeBikes, onHowItWorks, onRegister, Modifier.weight(1.1f),
                )
                HeroVisual(availableBikes, Modifier.weight(1f).height(400.dp))
            }
        } else {
            HeroCopy(
                titleSize, availableBikes, stations, lowestRate, signedIn,
                onSeeBikes, onHowItWorks, onRegister, Modifier,
            )
            Spacer(Modifier.height(32.dp))
            HeroVisual(availableBikes, Modifier.height(260.dp))
        }
    }
}

@Composable
private fun HeroCopy(
    titleSize: androidx.compose.ui.unit.TextUnit,
    availableBikes: Int,
    stations: Int,
    lowestRate: Double?,
    signedIn: Boolean,
    onSeeBikes: () -> Unit,
    onHowItWorks: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier,
) {
    val accent = MaterialTheme.colorScheme.primary
    Column(modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            "ALQUILER DE BICICLETAS POR HORAS",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 6.dp),
        )
        Text(
            text = buildAnnotatedString {
                append("Descubre tu ")
                withStyle(SpanStyle(color = accent)) { append("ruta ideal") }
                append(" sobre dos ruedas.")
            },
            fontSize = titleSize,
            lineHeight = titleSize * 1.05f,
            fontWeight = FontWeight.Black,
        )
        Text(
            "Reserva o alquila una bicicleta en las estaciones de tu ciudad. Elige tu estilo, " +
                "paga solo por las horas que pedaleas y devuélvela cuando termines.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSeeBikes) { Text("Ver bicicletas") }
            if (signedIn) {
                OutlinedButton(onClick = onHowItWorks) { Text("Cómo funciona") }
            } else {
                OutlinedButton(onClick = onRegister) { Text("Crear cuenta") }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp), modifier = Modifier.padding(top = 8.dp)) {
            HeroStat(availableBikes.toString(), "bicicletas disponibles")
            HeroStat(stations.toString(), "estaciones")
            lowestRate?.let { HeroStat(formatCurrency(it), "por hora desde") }
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String) {
    Column {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HeroVisual(availableBikes: Int, modifier: Modifier) {
    val photos by rememberStaticImages("hero")
    var current by remember { mutableIntStateOf(0) }
    LaunchedEffect(photos.size) {
        current = 0
        while (photos.size > 1) {
            delay(5000)
            current = (current + 1) % photos.size
        }
    }

    Box(modifier.fillMaxWidth()) {
        if (photos.isEmpty()) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            )
            BikeIllustration(
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = 5.dp,
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            )
        } else {
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                Crossfade(current, animationSpec = tween(900), label = "hero-photo") { index ->
                    StaticPhoto(photos[index.coerceIn(photos.indices)], Modifier.fillMaxSize())
                }
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f))),
                    ),
                )
                if (photos.size > 1) {
                    Row(
                        Modifier.align(Alignment.BottomEnd).padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        photos.indices.forEach { index ->
                            Box(
                                Modifier
                                    .size(if (index == current) 22.dp else 8.dp, 8.dp)
                                    .background(Color.White.copy(alpha = if (index == current) 1f else 0.5f), CircleShape),
                            )
                        }
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(Modifier.size(10.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                Column {
                    Text("$availableBikes disponibles", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "listas para rodar ahora",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
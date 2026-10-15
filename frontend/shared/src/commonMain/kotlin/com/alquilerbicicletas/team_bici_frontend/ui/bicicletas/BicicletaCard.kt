package com.alquilerbicicletas.team_bici_frontend.ui.bicicletas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.network.dto.BicicletaResponseDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.EstadoBicicleta
import com.alquilerbicicletas.team_bici_frontend.network.dto.TarifaResponseDto
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.components.BadgeTone
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikeIllustration
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikePhoto
import com.alquilerbicicletas.team_bici_frontend.ui.components.StatusBadge
import com.alquilerbicicletas.team_bici_frontend.ui.estadoLabel
import com.alquilerbicicletas.team_bici_frontend.ui.formatCurrency
import com.alquilerbicicletas.team_bici_frontend.ui.tipoLabel
import com.alquilerbicicletas.team_bici_frontend.ui.theme.BrandGradient

@Composable
fun BicicletaCard(
    bicicleta: BicicletaResponseDto,
    tarifa: TarifaResponseDto?,
    enabled: Boolean,
    onReserve: () -> Unit,
    onRent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier, lift = true) {
        BicicletaVisual(bicicleta)
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                "${bicicleta.marca} ${bicicleta.modelo}",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Código ${bicicleta.codigo} · ${bicicleta.estacionNombre}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (tarifa != null) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        formatCurrency(tarifa.valorHora),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        " / hora",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onReserve,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                ) { Text("Reservar") }
                Button(
                    onClick = onRent,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                ) { Text("Alquilar") }
            }
        }
    }
}

/** Header of the card: the bike photo, or the illustration when it has none. */
@Composable
private fun BicicletaVisual(bicicleta: BicicletaResponseDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(BrandGradient),
    ) {
        BikePhoto(
            url = bicicleta.imagenUrl,
            contentDescription = "${bicicleta.marca} ${bicicleta.modelo}",
            modifier = Modifier.fillMaxSize(),
            fallback = {
                BikeIllustration(
                    color = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp),
                )
            },
        )
        if (bicicleta.imagenUrl != null) {
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
        }
        Text(
            text = tipoLabel(bicicleta.tipo).uppercase(),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
        )
        StatusBadge(
            text = estadoLabel(bicicleta.estado.name),
            tone = if (bicicleta.estado == EstadoBicicleta.DISPONIBLE) BadgeTone.SUCCESS else BadgeTone.NEUTRAL,
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
        )
    }
}

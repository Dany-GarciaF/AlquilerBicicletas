package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.ui.components.AppCard
import com.alquilerbicicletas.team_bici_frontend.ui.theme.BrandGradient

/** Beneficio mostrado en la sección de ventajas del inicio. */
private data class Benefit(val title: String, val description: String)

private val Benefits = listOf(
    Benefit("Estaciones en la ciudad", "Encuentra la estación más cercana y retira tu bicicleta sin complicaciones."),
    Benefit("Reserva en segundos", "Asegura tu bicicleta para el día y la hora que necesitas."),
    Benefit("Tarifas claras", "Pagas por hora y el costo se calcula al devolver la bicicleta."),
    Benefit("Para cada trayecto", "Urbanas, de montaña, eléctricas y plegables en un solo lugar."),
    Benefit("Tu historial a la mano", "Consulta tus reservas y alquileres cuando quieras."),
)

/** Celda de la cuadrícula de beneficios: un beneficio o la tarjeta de llamada a la acción. */
private sealed interface BenefitCell {
    data class Item(val index: Int, val benefit: Benefit) : BenefitCell
    data object Cta : BenefitCell
}

@Composable
fun BenefitsSection(signedIn: Boolean, onPrimaryAction: () -> Unit) {
    val cells = Benefits.mapIndexed { index, benefit -> BenefitCell.Item(index + 1, benefit) } + BenefitCell.Cta
    SectionContainer(
        background = null,
        verticalPadding = 48.dp,
    ) {
        SectionTitle(
            title = "Pedalea con tranquilidad",
            subtitle = "Todo lo que necesitas para moverte en bicicleta, desde la reserva hasta la devolución.",
        )
        Spacer(Modifier.height(24.dp))
        ResponsiveRows(items = cells, columns = responsiveColumns()) { cell ->
            when (cell) {
                is BenefitCell.Item -> BenefitCard(cell.index, cell.benefit)
                BenefitCell.Cta -> ReadyCard(signedIn, onPrimaryAction)
            }
        }
    }
}

@Composable
private fun BenefitCard(index: Int, benefit: Benefit) {
    AppCard(modifier = Modifier.fillMaxWidth(), lift = true) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier.size(44.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    index.toString().padStart(2, '0'),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Black,
                )
            }
            Text(benefit.title, style = MaterialTheme.typography.titleLarge)
            Text(
                benefit.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReadyCard(signedIn: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandGradient, RoundedCornerShape(20.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("¿Listo para rodar?", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
        Text(
            if (signedIn) "Elige tu bicicleta y empieza ahora." else "Crea tu cuenta gratis y reserva tu primera bicicleta.",
            color = Color.White.copy(alpha = 0.92f),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14161A), contentColor = Color.White),
        ) { Text(if (signedIn) "Ver bicicletas" else "Crear cuenta") }
    }
}

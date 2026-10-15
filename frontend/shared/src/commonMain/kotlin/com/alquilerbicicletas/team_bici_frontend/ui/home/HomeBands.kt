package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikeIllustration
import com.alquilerbicicletas.team_bici_frontend.ui.theme.BrandGradient
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

internal val InkBand = Brush.linearGradient(listOf(Color(0xFF14161A), Color(0xFF1E2127)))

/** Paso de la sección «Cómo funciona». */
private data class Step(val number: String, val title: String, val description: String)

private val Steps = listOf(
    Step("1", "Crea tu cuenta", "Regístrate con tu correo o continúa con Google en pocos segundos."),
    Step("2", "Elige tu bicicleta", "Filtra por estación o tipo y reserva o inicia el alquiler al instante."),
    Step("3", "Pedalea y devuelve", "Disfruta el recorrido y devuélvela; el costo se calcula por las horas usadas."),
)

@Composable
fun HowItWorksSection() {
    SectionContainer(background = InkBand, verticalPadding = 56.dp) {
        Text("Cómo funciona", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Box(
            Modifier
                .padding(top = 10.dp, bottom = 28.dp)
                .height(4.dp)
                .widthIn(min = 44.dp, max = 44.dp)
                .background(Color(0xFFFF7A3D), androidx.compose.foundation.shape.RoundedCornerShape(50)),
        )
        ResponsiveRows(items = Steps, columns = if (LocalWindowSize.current == WindowSize.COMPACT) 1 else 3, spacing = 24.dp) { step ->
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(step.number, color = Color(0xFFFF7A3D), fontSize = 64.sp, fontWeight = FontWeight.Black)
                Text(step.title, color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text(step.description, color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

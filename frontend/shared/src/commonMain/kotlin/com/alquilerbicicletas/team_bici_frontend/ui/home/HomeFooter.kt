package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.ui.navigation.BrandMark
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

@Composable
fun HomeFooter(
    signedIn: Boolean,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBikes: () -> Unit,
    onStations: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    val compact = LocalWindowSize.current == WindowSize.COMPACT
    val linkColors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.78f))
    SectionContainer(background = InkBand, verticalPadding = 40.dp) {
        val brand: @Composable () -> Unit = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BrandMark(onDark = true)
                Text(
                    "Alquiler de bicicletas por horas en tu ciudad.",
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        val links: @Composable () -> Unit = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBikes, colors = linkColors) { Text("Bicicletas") }
                TextButton(onClick = onStations, colors = linkColors) { Text("Estaciones") }
                if (!signedIn) {
                    TextButton(onClick = onLogin, colors = linkColors) { Text("Ingresar") }
                    TextButton(onClick = onRegister, colors = linkColors) { Text("Registrarse") }
                }
            }
        }
        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                brand()
                links()
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                brand()
                links()
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color.White.copy(alpha = 0.14f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("© Team Bici", color = Color.White.copy(alpha = 0.55f), style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = onToggleTheme, colors = linkColors) {
                Text(if (darkTheme) "Modo claro" else "Modo oscuro")
            }
        }
    }
}

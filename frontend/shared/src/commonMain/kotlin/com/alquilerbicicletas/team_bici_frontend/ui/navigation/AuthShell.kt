package com.alquilerbicicletas.team_bici_frontend.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.ui.components.BikeIllustration
import com.alquilerbicicletas.team_bici_frontend.ui.theme.BrandGradient
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

/** Brand panel plus the sign-in form; stacks vertically on compact screens. */
@Composable
fun AuthShell(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    content: @Composable () -> Unit,
) {
    val compact = LocalWindowSize.current == WindowSize.COMPACT
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (compact) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                BrandPanel(compact = true, modifier = Modifier.fillMaxWidth().height(220.dp))
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.TopCenter) { content() }
            }
        } else {
            Row(Modifier.fillMaxSize()) {
                BrandPanel(compact = false, modifier = Modifier.weight(1f).fillMaxHeight())
                BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .heightIn(min = maxHeight)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) { content() }
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(8.dp),
        ) {
            ThemeToggleButton(darkTheme, onToggleTheme, compact, onBrandPanel = compact)
        }
    }
}

@Composable
private fun BrandPanel(compact: Boolean, modifier: Modifier) {
    Box(modifier.background(BrandGradient)) {
        BikeIllustration(
            color = Color.White.copy(alpha = 0.22f),
            strokeWidth = if (compact) 3.dp else 6.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(if (compact) 120.dp else 360.dp)
                .padding(horizontal = 24.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(if (compact) 24.dp else 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BrandMark(onDark = true)
            Spacer(Modifier.weight(1f))
            Text(
                text = "PEDALEA\nSIN LÍMITES",
                color = Color.White,
                fontSize = if (compact) 30.sp else 56.sp,
                lineHeight = if (compact) 32.sp else 58.sp,
                fontWeight = FontWeight.Black,
            )
            if (!compact) {
                Text(
                    "Reserva y alquila bicicletas en las estaciones de tu ciudad.",
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

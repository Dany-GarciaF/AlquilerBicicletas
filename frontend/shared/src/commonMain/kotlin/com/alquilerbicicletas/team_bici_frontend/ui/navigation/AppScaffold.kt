package com.alquilerbicicletas.team_bici_frontend.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alquilerbicicletas.team_bici_frontend.ui.components.FeedbackBanner
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.MaxContentWidth
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

/** Data shown in the account area of the top bar; null means the visitor has not signed in. */
data class AccountActions(
    val userName: String?,
    val onLogin: () -> Unit,
    val onRegister: () -> Unit,
    val onLogout: () -> Unit,
)

/**
 * Responsive shell shared by visitors and signed-in users: a top bar (with tabs on medium and
 * wide screens) and a bottom navigation bar on compact screens.
 */
@Composable
fun AppScaffold(
    account: AccountActions,
    destinations: List<AppDestination>,
    selected: AppDestination,
    onSelect: (AppDestination) -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    message: String?,
    messageIsError: Boolean,
    content: @Composable () -> Unit,
) {
    val windowSize = LocalWindowSize.current
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBar(account, destinations, selected, onSelect, darkTheme, onToggleTheme)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            content()
            if (message != null) {
                Box(
                    Modifier.align(Alignment.TopCenter).padding(horizontal = windowSize.pagePadding, vertical = 12.dp),
                ) { FeedbackBanner(message, isError = messageIsError) }
            }
        }
        if (windowSize.usesBottomNavigation) {
            BottomBar(destinations, selected, onSelect)
        }
    }
}

@Composable
private fun TopBar(
    account: AccountActions,
    destinations: List<AppDestination>,
    selected: AppDestination,
    onSelect: (AppDestination) -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    val windowSize = LocalWindowSize.current
    val compact = windowSize == WindowSize.COMPACT
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        Box(
            Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = MaxContentWidth)
                    .height(64.dp)
                    .padding(horizontal = windowSize.pagePadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrandMark(Modifier.clickable { onSelect(AppDestination.INICIO) })
                Spacer(Modifier.weight(1f))
                if (!compact) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        destinations.forEach { NavTab(it.label, it == selected) { onSelect(it) } }
                    }
                    Spacer(Modifier.weight(1f))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (account.userName != null) {
                        ThemeToggleButton(darkTheme, onToggleTheme, compact)
                        Avatar(account.userName)
                        OutlinedButton(onClick = account.onLogout) { Text(if (compact) "Salir" else "Cerrar sesión") }
                    } else {
                        if (!compact) ThemeToggleButton(darkTheme, onToggleTheme, compact = false)
                        TextButton(onClick = account.onLogin) { Text("Ingresar") }
                        Button(onClick = account.onRegister) { Text("Registrarse") }
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun BrandMark(modifier: Modifier = Modifier, onDark: Boolean = false) {
    Row(modifier) {
        Text(
            "TEAM ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = if (onDark) Color.White else MaterialTheme.colorScheme.onSurface,
        )
        Text(
            "BICI",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = if (onDark) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun NavTab(label: String, selected: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val textColor by animateColorAsState(
        when {
            selected -> MaterialTheme.colorScheme.onSurface
            hovered -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        tween(150),
        label = "tab-color",
    )
    val indicator by animateColorAsState(
        when {
            selected -> MaterialTheme.colorScheme.primary
            hovered -> MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            else -> Color.Transparent
        },
        tween(150),
        label = "tab-indicator",
    )
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .hoverable(interaction)
            .clickable(interactionSource = interaction, indication = null, role = Role.Tab, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = textColor)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.height(2.dp).width(24.dp).background(indicator, RoundedCornerShape(50)))
    }
}

@Composable
private fun BottomBar(
    destinations: List<AppDestination>,
    selected: AppDestination,
    onSelect: (AppDestination) -> Unit,
) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars)) {
            destinations.forEach { destination ->
                val active = destination == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(role = Role.Tab) { onSelect(destination) }
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .width(32.dp)
                            .height(3.dp)
                            .background(
                                if (active) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp),
                            ),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        destination.label,
                        fontSize = 11.sp,
                        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeToggleButton(darkTheme: Boolean, onToggle: () -> Unit, compact: Boolean, onBrandPanel: Boolean = false) {
    val colors = if (onBrandPanel) ButtonDefaults.textButtonColors(contentColor = Color.White) else ButtonDefaults.textButtonColors()
    TextButton(onClick = onToggle, colors = colors) {
        Text(
            when {
                darkTheme && compact -> "Claro"
                darkTheme -> "Modo claro"
                compact -> "Oscuro"
                else -> "Modo oscuro"
            },
        )
    }
}

@Composable
private fun Avatar(name: String) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            name.trim().firstOrNull()?.uppercase().orEmpty(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Black,
        )
    }
}

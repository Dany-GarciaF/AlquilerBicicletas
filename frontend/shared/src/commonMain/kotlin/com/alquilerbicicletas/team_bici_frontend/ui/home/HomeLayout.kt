package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.ui.components.Reveal
import com.alquilerbicicletas.team_bici_frontend.ui.theme.LocalWindowSize
import com.alquilerbicicletas.team_bici_frontend.ui.theme.MaxContentWidth
import com.alquilerbicicletas.team_bici_frontend.ui.theme.WindowSize

/** Number of columns used by card collections on the home page. */
@Composable
fun responsiveColumns(): Int = when (LocalWindowSize.current) {
    WindowSize.COMPACT -> 1
    WindowSize.MEDIUM -> 2
    WindowSize.EXPANDED -> 3
}

/**
 * Full-width band with centered content limited to [MaxContentWidth].
 * [background] paints the band edge to edge (used for colored sections).
 */
@Composable
fun SectionContainer(
    modifier: Modifier = Modifier,
    background: Brush? = null,
    verticalPadding: Dp = 48.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val windowSize = LocalWindowSize.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (background != null) Modifier.background(background) else Modifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = MaxContentWidth)
                .fillMaxWidth()
                .padding(horizontal = windowSize.pagePadding, vertical = verticalPadding),
            content = content,
        )
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Reveal(modifier.fillMaxWidth()) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Box(
            Modifier
                .padding(top = 2.dp)
                .height(4.dp)
                .widthIn(min = 44.dp, max = 44.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
        )
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    }
}

/** Lays items out in rows of [columns] equally sized cells; the last row is padded with empty cells. */
@Composable
fun <T> ResponsiveRows(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = 16.dp,
    itemContent: @Composable (T) -> Unit,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing)) {
        items.chunked(columns).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing)) {
                rowItems.forEachIndexed { col, item ->
                    Box(Modifier.weight(1f)) { Reveal(delayMillis = col * 120) { itemContent(item) } }
                }
                repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

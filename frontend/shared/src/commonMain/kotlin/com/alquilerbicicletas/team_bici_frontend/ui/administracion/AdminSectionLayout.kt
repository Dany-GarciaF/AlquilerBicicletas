package com.alquilerbicicletas.team_bici_frontend.ui.administracion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.ui.components.EmptyState
import com.alquilerbicicletas.team_bici_frontend.ui.components.FeedbackBanner
import com.alquilerbicicletas.team_bici_frontend.ui.components.PageGrid
import com.alquilerbicicletas.team_bici_frontend.ui.components.fullSpan
import com.alquilerbicicletas.team_bici_frontend.ui.components.skeletonItems

/** Shared page structure of every admin section: toolbar, feedback, loading / empty states and the cards. */
@Composable
fun AdminSectionLayout(
    header: @Composable () -> Unit,
    title: String,
    actionLabel: String,
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    emptyMessage: String,
    onCreate: () -> Unit,
    content: LazyGridScope.() -> Unit,
) {
    PageGrid(minCardWidth = 340.dp, header = header) {
        fullSpan("toolbar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Button(onClick = onCreate) { Text(actionLabel) }
            }
        }
        if (error != null) fullSpan("error") { FeedbackBanner(error, isError = true) }
        when {
            loading -> skeletonItems(count = 4, height = 170.dp)
            isEmpty -> fullSpan("empty") {
                EmptyState(title = "Sin registros", message = emptyMessage, actionLabel = actionLabel, onAction = onCreate)
            }
            else -> content()
        }
    }
}

@Composable
fun AdminCardBody(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) { content() }
}

package com.alquilerbicicletas.team_bici_frontend.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.alquilerbicicletas.team_bici_frontend.network.dto.TipoBicicleta
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.BikeActions
import com.alquilerbicicletas.team_bici_frontend.ui.catalog.CatalogState
import com.alquilerbicicletas.team_bici_frontend.ui.components.Reveal
import kotlinx.coroutines.launch

private const val HowItWorksIndex = 5

/** Public landing page: a single scrollable story from the hero to the footer. */
@Composable
fun HomeScreen(
    catalog: CatalogState,
    actions: BikeActions,
    signedIn: Boolean,
    listState: LazyListState,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onSeeBikes: () -> Unit,
    onSeeStations: () -> Unit,
    onSelectTipo: (TipoBicicleta) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        item(key = "hero") {
            HeroSection(
                availableBikes = catalog.bicicletas.size,
                stations = catalog.estaciones.size,
                lowestRate = catalog.lowestRate(),
                signedIn = signedIn,
                onSeeBikes = onSeeBikes,
                onHowItWorks = { scope.launch { listState.animateScrollToItem(HowItWorksIndex) } },
                onRegister = onRegister,
            )
        }
        item(key = "categories") { CategoriesSection(catalog, onSelectTipo) }
        item(key = "featured") { FeaturedBikesSection(catalog, actions, onSeeAll = onSeeBikes) }
        item(key = "stations-map") { StationsMapSection(catalog, onSeeStations) }
        item(key = "benefits") { Reveal { BenefitsSection(signedIn, onPrimaryAction = if (signedIn) onSeeBikes else onRegister) } }
        item(key = "how") { Reveal { HowItWorksSection() } }
        item(key = "routes") { RoutesSection() }
        item(key = "footer") {
            HomeFooter(
                signedIn = signedIn,
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onBikes = onSeeBikes,
                onStations = onSeeStations,
                onLogin = onLogin,
                onRegister = onRegister,
            )
        }
    }
}

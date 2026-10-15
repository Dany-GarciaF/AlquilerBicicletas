package com.alquilerbicicletas.team_bici_frontend.ui.navigation

/** Destinos de navegación de la app y las condiciones (sesión, rol) para mostrarlos. */
enum class AppDestination(
    val label: String,
    val requiresAuth: Boolean = false,
    val requiresAdministrator: Boolean = false,
) {
    INICIO("Inicio"),
    BICICLETAS("Bicicletas"),
    ESTACIONES("Estaciones"),
    RESERVAS("Reservas", requiresAuth = true),
    ALQUILERES("Alquileres", requiresAuth = true),
    ADMINISTRACION("Admin", requiresAuth = true, requiresAdministrator = true),
    ;

    fun isAvailableFor(signedIn: Boolean, isAdministrator: Boolean): Boolean =
        (!requiresAuth || signedIn) && (!requiresAdministrator || isAdministrator)

    companion object {
        fun availableFor(signedIn: Boolean, isAdministrator: Boolean): List<AppDestination> =
            entries.filter { it.isAvailableFor(signedIn, isAdministrator) }
    }
}
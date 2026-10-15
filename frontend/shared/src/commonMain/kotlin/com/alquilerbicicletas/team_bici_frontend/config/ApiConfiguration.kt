package com.alquilerbicicletas.team_bici_frontend.config

/** URL base de la API y resolución de las URL de imágenes, que pueden ser relativas o absolutas. */
object ApiConfiguration {
    private const val DEFAULT_BASE_URL = "http://localhost:8080"

    var baseUrl: String = DEFAULT_BASE_URL
        private set

    /** Image URLs may be relative to the API (local storage) or absolute (Cloudflare R2). */
    fun mediaUrl(url: String): String = if (url.startsWith("/")) baseUrl + url else url

    fun configure(baseUrl: String) {
        require(baseUrl.startsWith("http://") || baseUrl.startsWith("https://")) {
            "La URL base de la API debe utilizar HTTP o HTTPS"
        }
        this.baseUrl = baseUrl.trimEnd('/')
    }
}

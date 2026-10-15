package com.alquilerbicicletas.team_bici_frontend.auth.google

import io.ktor.http.Url

/** Resultado del regreso desde Google: código de autorización o error. */
sealed interface OAuthCallback {
    data class Success(val code: String) : OAuthCallback

    data class Failure(val reason: String) : OAuthCallback
}

/** Interpreta la URL de regreso (callback web o deep link de Android) y extrae el código o el error. */
object OAuthCallbackParser {
    const val CALLBACK_PATH = "auth/google/callback"

    /**
     * Interprets a URL (web location or Android deep link) as a Google sign-in callback.
     * Returns null when the URL is unrelated to the callback.
     */
    fun parse(url: String): OAuthCallback? {
        val withoutQuery = url.substringBefore('?').substringBefore('#').trimEnd('/')
        if (!withoutQuery.endsWith(CALLBACK_PATH)) return null

        val parameters = runCatching { Url(url).parameters }.getOrNull() ?: return null
        parameters["code"]?.takeIf { it.isNotBlank() }?.let { return OAuthCallback.Success(it) }
        return OAuthCallback.Failure(parameters["error"].orEmpty().ifBlank { "unknown" })
    }
}

package com.alquilerbicicletas.team_bici_frontend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alquilerbicicletas.team_bici_frontend.auth.google.GoogleSignInController
import com.alquilerbicicletas.team_bici_frontend.auth.google.GoogleSignInState
import com.alquilerbicicletas.team_bici_frontend.network.ApiRequestException
import com.alquilerbicicletas.team_bici_frontend.network.AuthRepository
import com.alquilerbicicletas.team_bici_frontend.network.dto.LoginRequestDto
import com.alquilerbicicletas.team_bici_frontend.network.dto.RegistroRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Modo de la pantalla de acceso: iniciar sesión o registrarse. */
enum class AuthMode {
    LOGIN,
    REGISTRO,
}

@Composable
fun AuthScreen(
    authRepository: AuthRepository,
    googleSignIn: GoogleSignInController? = null,
    initialMode: AuthMode = AuthMode.LOGIN,
    notice: String? = null,
    onBack: (() -> Unit)? = null,
) {
    var mode by remember(initialMode) { mutableStateOf(initialMode) }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var submitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val googleState by (googleSignIn?.state ?: IdleGoogleState).collectAsState()
    val googleExchanging = googleState is GoogleSignInState.Exchanging
    val locked = submitting || googleExchanging

    val isRegistration = mode == AuthMode.REGISTRO
    val submit: () -> Unit = {
        error = validateAuthInput(nombre, apellido, correo, contrasena, confirmacion, isRegistration)
        if (error == null) {
            scope.launch {
                submitting = true
                try {
                    if (isRegistration) {
                        authRepository.register(RegistroRequestDto(nombre.trim(), apellido.trim(), correo.trim(), contrasena))
                    } else {
                        authRepository.login(LoginRequestDto(correo.trim(), contrasena))
                    }
                } catch (exception: ApiRequestException) {
                    error = exception.fieldErrors.values.firstOrNull() ?: exception.message
                } finally {
                    submitting = false
                }
            }
        }
    }

    Column(
        modifier = Modifier.widthIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = if (isRegistration) "Crea tu cuenta" else "Inicia sesión",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = if (isRegistration) {
                "Regístrate para reservar y alquilar bicicletas."
            } else {
                "Accede para gestionar tus reservas y alquileres."
            },
            style = MaterialTheme.typography.bodyLarge,
        )
        if (notice != null) {
            Text(
                notice,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
                    .padding(12.dp),
            )
        }
        Spacer(Modifier.height(4.dp))

        if (isRegistration) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                enabled = !locked,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                enabled = !locked,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            enabled = !locked,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            enabled = !locked,
            singleLine = true,
            visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                    Text(if (mostrarContrasena) "Ocultar" else "Mostrar")
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        if (isRegistration) {
            OutlinedTextField(
                value = confirmacion,
                onValueChange = { confirmacion = it },
                label = { Text("Confirmar contraseña") },
                enabled = !locked,
                singleLine = true,
                visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                        Text(if (mostrarContrasena) "Ocultar" else "Mostrar")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        (error ?: (googleState as? GoogleSignInState.Failed)?.message)?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = submit,
            enabled = !locked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (submitting) {
                CircularProgressIndicator()
            } else {
                Text(if (isRegistration) "Crear cuenta" else "Ingresar")
            }
        }
        if (googleSignIn != null && googleSignIn.isAvailable) {
            GoogleSignInSection(
                exchanging = googleExchanging,
                enabled = !locked,
                onClick = {
                    error = null
                    googleSignIn.start()
                },
            )
        }
        TextButton(
            onClick = {
                mode = if (isRegistration) AuthMode.LOGIN else AuthMode.REGISTRO
                error = null
            },
            enabled = !locked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                if (isRegistration) {
                    "¿Ya tienes una cuenta? Inicia sesión"
                } else {
                    "¿No tienes cuenta? Regístrate"
                },
            )
        }
        if (onBack != null) {
            TextButton(onClick = onBack, enabled = !locked, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al inicio")
            }
        }
    }
}

private fun validateAuthInput(
    nombre: String,
    apellido: String,
    correo: String,
    contrasena: String,
    confirmacion: String,
    isRegistration: Boolean,
): String? = when {
    isRegistration && nombre.isBlank() -> "El nombre es obligatorio"
    isRegistration && apellido.isBlank() -> "El apellido es obligatorio"
    correo.isBlank() -> "El correo electrónico es obligatorio"
    !correo.contains("@") -> "Ingresa un correo electrónico válido"
    contrasena.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
    isRegistration && contrasena != confirmacion -> "Las contraseñas no coinciden"
    else -> null
}

@Composable
private fun GoogleSignInSection(
    exchanging: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text("o", style = MaterialTheme.typography.labelMedium)
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(if (exchanging) "Verificando con Google..." else "Continuar con Google")
    }
}

private val IdleGoogleState: StateFlow<GoogleSignInState> = MutableStateFlow(GoogleSignInState.Idle)

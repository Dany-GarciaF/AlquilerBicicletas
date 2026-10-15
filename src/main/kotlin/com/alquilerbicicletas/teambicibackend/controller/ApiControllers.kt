package com.alquilerbicicletas.teambicibackend.controller

import com.alquilerbicicletas.teambicibackend.api.dto.alquiler.*
import com.alquilerbicicletas.teambicibackend.api.dto.auth.*
import com.alquilerbicicletas.teambicibackend.api.dto.bicicleta.*
import com.alquilerbicicletas.teambicibackend.api.dto.estacion.*
import com.alquilerbicicletas.teambicibackend.api.dto.reserva.*
import com.alquilerbicicletas.teambicibackend.api.dto.tarifa.*
import com.alquilerbicicletas.teambicibackend.domain.enums.EstadoBicicleta
import com.alquilerbicicletas.teambicibackend.security.UsuarioPrincipal
import com.alquilerbicicletas.teambicibackend.service.*
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/** Endpoints de autenticación: registro, login, renovación de token e intercambio del código de Google. */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val service: AuthService,
    private val googleOAuth2Service: GoogleOAuth2Service,
) {
    @PostMapping("/registro") @ResponseStatus(HttpStatus.CREATED)
    fun registro(@Valid @RequestBody dto: RegistroRequestDto) = service.registrar(dto)
    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: LoginRequestDto) = service.iniciarSesion(dto)
    @PostMapping("/oauth2/exchange")
    fun exchangeGoogleCode(@Valid @RequestBody dto: OAuthCodeExchangeRequestDto) =
        googleOAuth2Service.intercambiarCodigo(dto.code)
    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody dto: RefreshTokenRequestDto) = service.refresh(dto.refreshToken)
    @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@AuthenticationPrincipal usuario: UsuarioPrincipal) = service.logout(usuario.usuarioId)
}

/** Endpoints de estaciones: lectura pública y alta/edición solo para administradores. */
@RestController
@RequestMapping("/api/estaciones")
class EstacionController(private val service: EstacionService) {
    @GetMapping fun listar() = service.listar()
    @GetMapping("/{id}") fun obtener(@PathVariable id: Long) = service.obtener(id)
    @PostMapping @PreAuthorize("hasRole('ADMINISTRADOR')") @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody dto: EstacionRequestDto) = service.crear(dto)
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody dto: EstacionRequestDto) = service.actualizar(id, dto)
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}

/** Endpoints de tarifas por tipo de bicicleta: lectura pública y edición solo para administradores. */
@RestController
@RequestMapping("/api/tarifas")
class TarifaController(private val service: TarifaService) {
    @GetMapping fun listar() = service.listar()
    @PostMapping @PreAuthorize("hasRole('ADMINISTRADOR')") @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody dto: TarifaRequestDto) = service.crear(dto)
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody dto: TarifaRequestDto) = service.actualizar(id, dto)
}

/** Endpoints de bicicletas: catálogo, estado, y subida o eliminación de su imagen (administradores). */
@RestController
@RequestMapping("/api/bicicletas")
class BicicletaController(private val service: BicicletaService) {
    @GetMapping fun listar() = service.listar()
    @GetMapping("/disponibles") fun disponibles(@RequestParam(required = false) estacionId: Long?) = service.disponibles(estacionId)
    @GetMapping("/{id}") fun obtener(@PathVariable id: Long) = service.obtener(id)
    @PostMapping @PreAuthorize("hasRole('ADMINISTRADOR')") @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody dto: BicicletaRequestDto) = service.crear(dto)
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun actualizar(@PathVariable id: Long, @Valid @RequestBody dto: BicicletaRequestDto) = service.actualizar(id, dto)
    @PatchMapping("/{id}/estado") @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun estado(@PathVariable id: Long, @RequestParam estado: EstadoBicicleta) = service.cambiarEstado(id, estado)
    @PostMapping("/{id}/imagen", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]) @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun subirImagen(@PathVariable id: Long, @RequestParam("archivo") archivo: MultipartFile) = service.actualizarImagen(id, archivo.bytes)
    @DeleteMapping("/{id}/imagen") @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun eliminarImagen(@PathVariable id: Long) = service.eliminarImagen(id)
}

/** Endpoints para que el usuario autenticado cree, consulte y cancele sus reservas. */
@RestController
@RequestMapping("/api/reservas")
class ReservaController(private val service: ReservaService) {
    @GetMapping fun listar(@AuthenticationPrincipal usuario: UsuarioPrincipal) = service.listarPorUsuario(usuario.usuarioId)
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    fun crear(@AuthenticationPrincipal usuario: UsuarioPrincipal, @Valid @RequestBody dto: ReservaRequestDto) = service.crear(usuario.usuarioId, dto)
    @PatchMapping("/{id}/cancelar")
    fun cancelar(@PathVariable id: Long, @AuthenticationPrincipal usuario: UsuarioPrincipal) = service.cancelar(id, usuario.usuarioId)
}

/** Endpoints para que el usuario autenticado inicie un alquiler, consulte los suyos y los devuelva. */
@RestController
@RequestMapping("/api/alquileres")
class AlquilerController(private val service: AlquilerService) {
    @GetMapping fun listar(@AuthenticationPrincipal usuario: UsuarioPrincipal) = service.listarPorUsuario(usuario.usuarioId)
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    fun crear(@AuthenticationPrincipal usuario: UsuarioPrincipal, @Valid @RequestBody dto: AlquilerRequestDto) = service.crear(usuario.usuarioId, dto)
    @PatchMapping("/{id}/devolucion")
    fun devolver(@PathVariable id: Long, @AuthenticationPrincipal usuario: UsuarioPrincipal, @Valid @RequestBody dto: DevolucionRequestDto) = service.devolver(id, usuario.usuarioId, dto)
}

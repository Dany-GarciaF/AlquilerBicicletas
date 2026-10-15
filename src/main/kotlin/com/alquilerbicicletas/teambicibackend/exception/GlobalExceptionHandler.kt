package com.alquilerbicicletas.teambicibackend.exception

import jakarta.validation.ConstraintViolationException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException

/** Traduce las excepciones de la aplicación y de validación a respuestas HTTP con formato ErrorResponse. */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(exception: UnauthorizedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> =
        response(HttpStatus.UNAUTHORIZED, exception.message, request)

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(
        exception: AuthenticationException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        response(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos", request)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(
        exception: ResourceNotFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = response(HttpStatus.NOT_FOUND, exception.message, request)

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(
        exception: BusinessException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = response(HttpStatus.CONFLICT, exception.message, request)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val errors = exception.bindingResult.allErrors.associate { error ->
            val field = (error as? FieldError)?.field ?: error.objectName
            field to (error.defaultMessage ?: "Valor inválido")
        }
        return response(HttpStatus.BAD_REQUEST, "Error de validación", request, errors)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        exception: ConstraintViolationException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        response(
            HttpStatus.BAD_REQUEST,
            "Error de validación",
            request,
            exception.constraintViolations.associate {
                it.propertyPath.toString() to it.message
            },
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableMessage(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        response(HttpStatus.BAD_REQUEST, "El formato de la solicitud no es válido", request)

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        exception: MethodArgumentTypeMismatchException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val expectedType = exception.requiredType?.simpleName?.lowercase() ?: "tipo esperado"
        return response(
            HttpStatus.BAD_REQUEST,
            "El parámetro '${exception.name}' debe ser de tipo $expectedType",
            request,
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(
        exception: MissingServletRequestParameterException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        response(HttpStatus.BAD_REQUEST, "Falta el parámetro obligatorio '${exception.parameterName}'", request)

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
        exception: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        response(HttpStatus.METHOD_NOT_ALLOWED, "El método HTTP no está permitido para este recurso", request)

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupported(
        exception: HttpMediaTypeNotSupportedException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        response(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "El tipo de contenido debe ser application/json", request)

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(
        exception: DataIntegrityViolationException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Violación de integridad en {}: {}", request.requestURI, exception.mostSpecificCause.message)
        return response(HttpStatus.CONFLICT, integrityMessage(exception), request)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(
        exception: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error("Error no controlado en ${request.requestURI}", exception)
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno en el servidor", request)
    }

    private fun response(
        status: HttpStatus,
        message: String?,
        request: HttpServletRequest,
        errors: Map<String, String>? = null,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                message = message ?: status.reasonPhrase,
                path = request.requestURI,
                errors = errors,
            ),
        )

    private fun integrityMessage(exception: DataIntegrityViolationException): String {
        val cause = exception.mostSpecificCause.message.orEmpty()
        return when {
            cause.contains("uk_usuario_correo", ignoreCase = true) -> "El correo electrónico ya está registrado"
            cause.contains("bicicletas_codigo", ignoreCase = true) -> "El código de bicicleta ya está registrado"
            cause.contains("estaciones_codigo", ignoreCase = true) -> "El código de estación ya está registrado"
            cause.contains("tarifas_tipo_bicicleta", ignoreCase = true) -> "Ya existe una tarifa para este tipo de bicicleta"
            else -> "La operación viola una restricción de integridad de los datos"
        }
    }
}

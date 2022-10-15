package styli.handler

import org.hibernate.exception.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import styli.exceptions.InvalidFileTypeException
import styli.exceptions.ProfileNotFoundException
import styli.image.dto.response.InvalidFileTypeResponse
import styli.profile.dto.ProfileNotFoundResponse
import styli.security.dto.response.ConstraintViolationExceptionResponse
import styli.security.dto.response.ValidationExceptionResponse

@ControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<List<ValidationExceptionResponse>> {
        logger.error(ex.message)
        return ResponseEntity.status(BAD_REQUEST).body(
            ex.fieldErrors.map { fieldError: FieldError? ->
                ValidationExceptionResponse(fieldError?.field, fieldError?.defaultMessage)
            }.toList()
        )
    }

    @ExceptionHandler
    fun handleConstraintViolation(ex: DataIntegrityViolationException): ResponseEntity<ConstraintViolationExceptionResponse> {
        logger.error(ex.message)
        val cause: Throwable? = ex.cause
        if (cause is ConstraintViolationException) {
            return ResponseEntity.status(BAD_REQUEST).body(
                ConstraintViolationExceptionResponse(
                    getUniqueConstraintValidationMessage(cause.constraintName ?: "")
                )
            )
        }
        return ResponseEntity.status(BAD_REQUEST).body(
            ConstraintViolationExceptionResponse("Constraint violation")
        )
    }

    @ExceptionHandler
    fun handleInvalidFileTypeException(ex: InvalidFileTypeException): ResponseEntity<InvalidFileTypeResponse> {
        logger.error(ex.message)
        return ResponseEntity.status(BAD_REQUEST).body(InvalidFileTypeResponse(ex.message))
    }

    @ExceptionHandler
    fun handleProfileNotFoundException(ex: ProfileNotFoundException): ResponseEntity<ProfileNotFoundResponse> {
        logger.error(ex.message)
        return ResponseEntity.status(BAD_REQUEST).body(ProfileNotFoundResponse(ex.message))
    }

    private fun getUniqueConstraintValidationMessage(message: String): String {
        if (message.contains("user")) {
            return "Username already in use"
        }
        if (message.contains("profile")) {
            return "Email already in use"
        }
        return "Constraint validation"
    }
}
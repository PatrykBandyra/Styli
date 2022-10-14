package styli.security.dto.response

data class ValidationExceptionResponse(
    val field: String?,
    val defaultMessage: String?,
)

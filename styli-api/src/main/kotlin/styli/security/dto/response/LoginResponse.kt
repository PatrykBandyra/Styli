package styli.security.dto.response

data class LoginResponse(
    val token: String,
    val expiresAt: Long,
)

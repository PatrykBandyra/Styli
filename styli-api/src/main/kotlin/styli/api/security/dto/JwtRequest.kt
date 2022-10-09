package styli.api.security.dto

data class JwtRequest(
    val username: String,
    val password: String
)
package styli.api.security.dto

data class LoginRequest(
    var username: String,
    var password: String
)
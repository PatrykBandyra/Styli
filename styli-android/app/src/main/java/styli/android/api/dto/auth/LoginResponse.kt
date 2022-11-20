package styli.android.api.dto.auth

data class LoginResponse(
    val expiresAt: Int,
    val token: String
)
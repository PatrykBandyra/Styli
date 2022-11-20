package styli.android.api.dto.auth

data class RegisterResponse(
    val email: String,
    val name: String,
    val surname: String,
    val username: String
)
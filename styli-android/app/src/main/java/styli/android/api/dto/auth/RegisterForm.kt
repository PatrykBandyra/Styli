package styli.android.api.dto.auth

data class RegisterForm(
    val email: String,
    val name: String,
    val password: String,
    val surname: String,
    val username: String
)
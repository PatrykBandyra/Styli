package styli.android.api.dto.auth

data class RegisterForm(
    val username: String,
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
)
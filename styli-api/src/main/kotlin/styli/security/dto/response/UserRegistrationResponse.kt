package styli.security.dto.response

data class UserRegistrationResponse(
    val username: String,
    val email: String,
    val name: String,
    val surname: String
)

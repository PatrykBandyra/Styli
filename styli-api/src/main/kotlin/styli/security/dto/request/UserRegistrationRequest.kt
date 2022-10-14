package styli.security.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class UserRegistrationRequest(
    @field:NotBlank(message = "Username cannot be blank")
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8, max = 24, message = "Password length must be between 8 and 24 characters")
    val password: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Invalid email formatting")
    val email: String,

    @field:NotBlank(message = "Name cannot be blank")
    val name: String,

    @field:NotBlank(message = "Surname cannot be blank")
    val surname: String,
)
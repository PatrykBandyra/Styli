package styli.api.security.dto

import styli.api.security.model.User

data class JwtResponse(
    val userDetails: User,
    val jwtToken: String,
)

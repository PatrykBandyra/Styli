package styli.api.security.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import styli.api.security.dto.JwtRequest
import styli.api.security.dto.JwtResponse
import styli.api.security.service.JwtService

@RestController
@RequestMapping("\${styli.rootUrl}/jwt")
class JwtController(
    private val jwtService: JwtService
) {

    @PostMapping
    fun createJwtToken(@RequestBody jwtRequest: JwtRequest): JwtResponse {
        return jwtService.createJwtToken(jwtRequest)
    }
}
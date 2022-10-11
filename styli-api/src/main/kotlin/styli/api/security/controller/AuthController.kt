package styli.api.security.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import styli.api.security.dto.LoginRequest
import styli.api.security.service.TokenService

@RestController
@RequestMapping("\${styli.rootUrl}/auth")
class AuthController(
    private val tokenService: TokenService,
    private val authManager: AuthenticationManager,
) {

    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/token")
    fun getToken(@RequestBody loginRequest: LoginRequest): ResponseEntity<String> {
        logger.info("Token requested for ${loginRequest.username}")
        val authentication: Authentication =
            authManager.authenticate(UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password))
        val token: String = tokenService.generateToken(authentication)
        logger.info("Token has been generated: $token")
        return ResponseEntity.ok(token)
    }

}
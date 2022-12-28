package styli.security.controller

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
import styli.security.dto.request.LoginRequest
import styli.security.dto.request.UserRegistrationRequest
import styli.security.dto.response.LoginResponse
import styli.security.dto.response.UserRegistrationResponse
import styli.security.service.ApplicationUserService
import styli.security.service.TokenService
import javax.validation.Valid

@RestController
@RequestMapping("\${styli.rootUrl}/auth")
class AuthController(
    private val tokenService: TokenService,
    private val authManager: AuthenticationManager,
    private val applicationUserService: ApplicationUserService,
) {

    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("login")
    fun getToken(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        logger.info("Token requested for ${loginRequest.username}")
        val authentication: Authentication =
            authManager.authenticate(UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password))
        val loginResponse: LoginResponse = tokenService.generateToken(authentication)
        return ResponseEntity.ok(loginResponse)
    }

    @PostMapping("register")
    fun registerUser(@Valid @RequestBody registrationRequest: UserRegistrationRequest): ResponseEntity<UserRegistrationResponse> {
        val savedProfile = applicationUserService.registerUser(registrationRequest)
        return ResponseEntity.ok(
            UserRegistrationResponse(
                savedProfile.user.username,
                savedProfile.email,
                savedProfile.name,
                savedProfile.surname
            )
        )
    }

}
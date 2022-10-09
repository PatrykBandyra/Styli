package styli.api.security.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import styli.api.security.dto.JwtRequest
import styli.api.security.dto.JwtResponse
import styli.api.security.model.Role
import styli.api.security.model.User
import styli.api.security.repository.UserRepository
import styli.api.security.util.JwtUtil

@Service
class JwtService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val authManager: AuthenticationManager,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: User? = userRepository.findByUsername(username)
        if (user != null) {
            return org.springframework.security.core.userdetails.User(
                user.username,
                user.password,
                getAuthorities(user)
                )
        } else {
            throw UsernameNotFoundException("Username is not valid")
        }
    }

    fun createJwtToken(jwtRequest: JwtRequest): JwtResponse {
        authenticate(jwtRequest.username, jwtRequest.password)
        val userDetails: UserDetails = loadUserByUsername(jwtRequest.username)
        val generatedToken: String = jwtUtil.generateToken(userDetails)
        val user: User? = userRepository.findByUsername(jwtRequest.username)
        return JwtResponse(user!!, generatedToken)
    }

    private fun getAuthorities(user: User): MutableSet<GrantedAuthority> {
        return user.roles.map { role: Role ->
            return@map SimpleGrantedAuthority("ROLE_${role.name}")
        }.toMutableSet()
    }

    private fun authenticate(username: String, password: String) {
        try {
            authManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw Exception("User is disabled")
        } catch (e: BadCredentialsException) {
            throw Exception("Bad user credentials")
        }
    }
}
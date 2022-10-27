package styli.security.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import styli.security.dto.response.LoginResponse
import java.time.Instant

@Service
class TokenService(
    private val encoder: JwtEncoder,
    @Value("\${styli.jwt.expirationInSeconds}") private val jwtExpirationInSeconds: Long,
) {

    fun generateToken(authentication: Authentication): LoginResponse {
        val now: Instant = Instant.now()
        val scope: Set<String> = authentication.authorities
            .map(GrantedAuthority::getAuthority)
            .toSet()
        val expiresAt = now.plusSeconds(jwtExpirationInSeconds)
        val claims: JwtClaimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(authentication.name)
            .claim("scope", scope)
            .build()
        return LoginResponse(this.encoder.encode(JwtEncoderParameters.from(claims)).tokenValue, expiresAt.epochSecond)
    }
}
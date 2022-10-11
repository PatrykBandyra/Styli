package styli.api.security.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TokenService(
    private val encoder: JwtEncoder,
) {

    fun generateToken(authentication: Authentication): String {
        val now: Instant = Instant.now()
        val scope: Set<String> = authentication.authorities
            .map(GrantedAuthority::getAuthority)
            .toSet()
        val claims: JwtClaimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .subject(authentication.name)
            .claim("scope", scope)
            .build()
        return this.encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
package styli.api.security.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import styli.api.security.config.StyliConfig
import java.util.Base64
import java.util.Date

@Component
class JwtUtil(
    private val styliConfig: StyliConfig,
) {

    companion object {
        private const val DEFAULT_TOKEN_VALIDITY_IN_SECS: Int = 3600
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username: String = getUsernameFromToken(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() +
                    (styliConfig.tokenValidityInSecs ?: DEFAULT_TOKEN_VALIDITY_IN_SECS)))
            .signWith(Keys.hmacShaKeyFor(Base64.getEncoder().encode(styliConfig.secretKey?.toByteArray())))
            .compact()
    }

    private fun <T> getClaimFromToken(token: String, claimResolver: (Claims) -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(Base64.getEncoder().encode(styliConfig.secretKey?.encodeToByteArray())).build()
            .parseClaimsJws(token).body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expirationDate: Date = getExpirationDateFromToken(token)
        return expirationDate.before(Date())
    }

    private fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token, Claims::getExpiration)
    }
}
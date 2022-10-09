package styli.api.security.configuration

import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import styli.api.security.service.JwtService
import styli.api.security.util.JwtUtil
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtRequestFilter(
    private val jwtUtil: JwtUtil,
    private val jwtService: JwtService,
) : OncePerRequestFilter() {

    companion object {
        private val logger = LoggerFactory.getLogger(Companion::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header: String? = request.getHeader("Authorization")
        var jwtToken: String? = null
        var username: String? = null
        if (header != null && header.startsWith("Bearer ")) {
            jwtToken = header.substring(7)
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken)
            } catch (e: IllegalArgumentException) {
                logger.error("Unable to get JWT token. \nException: ${e.message}")
            } catch (e: ExpiredJwtException) {
                logger.error("JWT token is expired. \nException: ${e.message}")
            }
        } else {
            logger.error("Jwt token does not start with 'Bearer '")
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails: UserDetails = jwtService.loadUserByUsername(username)
            if (jwtUtil.validateToken(jwtToken!!, userDetails)) {
                val usernamePasswordAuthToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                usernamePasswordAuthToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthToken
            }
        }

        filterChain.doFilter(request, response)
    }
}
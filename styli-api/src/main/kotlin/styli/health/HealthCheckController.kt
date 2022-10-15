package styli.health

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("\${styli.rootUrl}/health")
class HealthCheckController {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(HealthCheckController::class.java)
    }

    @GetMapping
    fun getHealthCheck(): ResponseEntity<Map<String, String>> {
        logger.info("[API]: Health check endpoint called")
        val response: Map<String, String> = mapOf("message" to "[API]: Health check works")
        return ResponseEntity.ok(response)
    }

    @GetMapping("auth")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    fun getAuthenticatedUserUsername(principal: Principal): ResponseEntity<Map<String, String>> {
        logger.info(
            "[API]: Auth health check endpoint called for user '${principal.name}' " +
                    "with authorities: ${SecurityContextHolder.getContext().authentication.authorities}"
        )
        val response: Map<String, String> = mapOf("message" to "[API]: Hello, ${principal.name}")
        return ResponseEntity.ok(response)
    }
}
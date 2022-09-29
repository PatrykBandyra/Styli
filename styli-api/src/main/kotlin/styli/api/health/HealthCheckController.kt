package styli.api.health

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthCheckController {
    @GetMapping
    fun getHealthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("Health check works")
    }
}
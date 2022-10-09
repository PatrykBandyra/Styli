package styli.api.health

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${styli.rootUrl}/health")
class HealthCheckController {
    @GetMapping
    fun getHealthCheck(): ResponseEntity<Map<String, String>> {
        val response: Map<String, String> = mapOf("message" to "[API]: Health check works")
        return ResponseEntity.ok(response)
    }
}
package styli.effects

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import styli.effects.EffectsConfig.Effect

@RestController
@RequestMapping("\${styli.rootUrl}/effect")
class EffectsController(
    private val effectsConfig: EffectsConfig,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(EffectsController::class.java)
    }

    @GetMapping
    fun getAvailableEffects(): ResponseEntity<List<String>> {
        val effects = effectsConfig.effects.map { effect: Effect ->
            val response: HttpStatus? = WebClient.create(effect.healthUrl).get()
                .exchangeToMono { response: ClientResponse -> Mono.just(response.statusCode()) }
                .onErrorComplete()
                .block()
            if (response == OK) {
                return@map effect.name
            }
            logger.error("${effect.name} service is down")
            return@map null
        }.filterNotNull()
        return ResponseEntity.ok(effects)
    }
}
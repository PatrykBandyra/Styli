package styli.effects.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import styli.effects.config.EffectsConfig
import styli.effects.dto.request.EffectRequest
import styli.exceptions.EffectServiceDownException
import styli.exceptions.InvalidEffectNameException

@Service
class EffectsService(
    private val effectsConfig: EffectsConfig,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(EffectsService::class.java)
    }

    fun getAvailableEffects(): List<String> {
        val effects: List<String> = effectsConfig.effects.map { effect: EffectsConfig.Effect ->
            logger.info("URL: ${effect.healthUrl}")
            val response: HttpStatus? = WebClient.create(effect.healthUrl).get()
                .exchangeToMono { response: ClientResponse -> Mono.just(response.statusCode()) }
                .onErrorComplete()
                .block()
            if (response == HttpStatus.OK) {
                return@map effect.name
            }
            logger.error("${effect.name} service is down")
            return@map null
        }.filterNotNull()
        return effects
    }

    fun applyEffectToImage(effectRequest: EffectRequest, image: MultipartFile, image2: MultipartFile?): ByteArray? {
        try {
            val effectUrl =
                effectsConfig.effects.first { effect: EffectsConfig.Effect -> effect.name == effectRequest.effectName }.url
            val multipartBodyBuilder = MultipartBodyBuilder()
            multipartBodyBuilder.part("image", image.resource)
            if (image2 != null) {
                multipartBodyBuilder.part("bg_image", image2.resource)
            }
            effectRequest.effectParams.forEach { effectParam: EffectRequest.EffectParam ->
                multipartBodyBuilder.part(effectParam.name, effectParam.value)
            }
            logger.info("URL: $effectUrl")
            return WebClient.create(effectUrl)
                .post()
                .uri { builder: UriBuilder ->
                    effectRequest.effectParams.forEach { effectParam: EffectRequest.EffectParam ->
                        builder.queryParam(effectParam.name, effectParam.value)
                    }
                    builder.build()
                }
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .retrieve()
                .bodyToMono(ByteArray::class.java)
                .block()
        } catch (_: NoSuchElementException) {
            throw InvalidEffectNameException("Effect with name ${effectRequest.effectName} does not exist")
        } catch (e: WebClientRequestException) {
            logger.error(e.message)
            throw EffectServiceDownException("Service applying effect ${effectRequest.effectName} is down")
        }
    }
}
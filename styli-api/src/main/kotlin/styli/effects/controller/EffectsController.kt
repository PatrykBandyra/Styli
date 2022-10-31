package styli.effects.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import styli.effects.dto.request.EffectRequest
import styli.effects.dto.response.EffectResponse
import styli.effects.service.EffectsService

@RestController
@RequestMapping("\${styli.rootUrl}/effect")
class EffectsController(
    private val effectsService: EffectsService,
) {

    @GetMapping
    fun getAvailableEffects(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(effectsService.getAvailableEffects())
    }

    @PostMapping
    fun applyEffectToImage(
        @RequestPart effectRequest: EffectRequest,
        @RequestPart image: MultipartFile,
        @RequestPart image2: MultipartFile? = null,
    ): ResponseEntity<EffectResponse> {
        return ResponseEntity.ok(EffectResponse(effectsService.applyEffectToImage(effectRequest, image, image2)))
    }
}
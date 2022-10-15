package styli.image.controller

import org.springframework.data.domain.Page
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import styli.exceptions.InvalidFileTypeException
import styli.image.dto.ImageDto
import styli.image.dto.request.ImageCreationRequest
import styli.image.repository.ImageRepository
import styli.image.service.ImageService
import java.security.Principal

@RestController
@RequestMapping("\${styli.rootUrl}/image")
class ImageController(
    private val imageService: ImageService,
    private val imageRepository: ImageRepository,
) {

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_CREATE')")
    fun uploadImage(
        @RequestPart imageDetails: ImageCreationRequest,
        @RequestPart image: MultipartFile,
        principal: Principal,
    ): ResponseEntity<in Nothing> {
        val supportedContentTypes = listOf(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE)
        if (!supportedContentTypes.contains(image.contentType)) {
            throw InvalidFileTypeException("Invalid content type. Content type provided: ${image.contentType}. Supported types: $supportedContentTypes")
        }
        imageService.uploadImage(principal.name, image, imageDetails.description)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    fun getImagesPaginated(
        @RequestParam page: Int,
        @RequestParam size: Int,
        principal: Principal,
    ): ResponseEntity<Page<ImageDto>> {
        return ResponseEntity.ok(imageService.getImagesByUsernamePaged(principal.name, page, size))
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    fun deleteImage(@RequestParam imageId: Long, principal: Principal): ResponseEntity<in Nothing> {
        imageRepository.deleteByIdAndProfileUserUsername(imageId, principal.name)
        return ResponseEntity.ok().build()
    }
}
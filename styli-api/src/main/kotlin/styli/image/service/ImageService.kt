package styli.image.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import styli.exceptions.ProfileNotFoundException
import styli.image.dto.ImageDto
import styli.image.model.Image
import styli.image.repository.ImageRepository
import styli.profile.repository.ProfileRepository

@Service
class ImageService(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
) {
    fun uploadImage(username: String, image: MultipartFile, imageDescription: String?) {
        val profile = profileRepository.findByUserUsername(username)
            ?: throw ProfileNotFoundException("Profile for user with username $username not found")
        profile.images.add(Image(image = image.bytes, description = imageDescription, profile = profile))
        profileRepository.save(profile)
    }

    fun getImagesByUsernamePaged(username: String, page: Int, size: Int): Page<ImageDto> {
        val pageRequest = PageRequest.of(page, size)
        return imageRepository.findAllByProfileUserUsername(username, pageRequest)
            .map { image: Image -> ImageDto(image.image, image.description) }
    }
}
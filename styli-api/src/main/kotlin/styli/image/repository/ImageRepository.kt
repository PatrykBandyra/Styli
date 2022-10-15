package styli.image.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import styli.image.model.Image

@Repository
interface ImageRepository : JpaRepository<Image, Long> {

    fun findAllByProfileUserUsername(username: String, pageable: Pageable): Page<Image>
    fun deleteByIdAndProfileUserUsername(imageId: Long, username: String)
}
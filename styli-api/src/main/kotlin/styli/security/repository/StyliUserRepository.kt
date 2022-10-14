package styli.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import styli.security.model.StyliUser

@Repository
interface StyliUserRepository : JpaRepository<StyliUser, Int> {

    fun findStyliUserByUsername(username: String?): StyliUser?
    fun existsByUsername(username: String?): Boolean
}
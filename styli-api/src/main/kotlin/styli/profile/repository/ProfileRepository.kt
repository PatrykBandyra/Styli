package styli.profile.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import styli.profile.model.Profile

@Repository
interface ProfileRepository : JpaRepository<Profile, Int> {

    fun existsByEmail(email: String?): Boolean
}
package styli.api.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import styli.api.security.model.User

@Repository
interface UserRepository : JpaRepository<User, String> {

    fun findByUsername(username: String): User?
}
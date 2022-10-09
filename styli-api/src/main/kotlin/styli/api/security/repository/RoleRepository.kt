package styli.api.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import styli.api.security.model.Role

@Repository
interface RoleRepository : JpaRepository<Role, String> {

    fun findByName(name: String): Role?
}
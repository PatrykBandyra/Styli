package styli.api.security.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import styli.api.security.model.Role
import styli.api.security.repository.RoleRepository

@RestController
@RequestMapping("\${styli.rootUrl}/role")
class RoleController(
    private val roleRepository: RoleRepository
) {

    @PostMapping
    fun createNewRole(@RequestBody role: Role): Role {
        return roleRepository.save(role)
    }
}
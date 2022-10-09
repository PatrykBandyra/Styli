package styli.api.security.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import styli.api.security.model.User
import styli.api.security.repository.UserRepository

@RestController
@RequestMapping("\${styli.rootUrl}/user")
class UserController(
    private val userRepository: UserRepository,
) {

    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return userRepository.save(user)
    }
}
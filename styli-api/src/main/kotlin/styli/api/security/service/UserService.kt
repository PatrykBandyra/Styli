package styli.api.security.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import styli.api.security.model.User
import styli.api.security.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun createUser(user: User): User {
        val newUser = User(id = null, username = user.username, password = passwordEncoder.encode(user.password))
        newUser.roles.addAll(user.roles)
        return userRepository.save(newUser)
    }
}
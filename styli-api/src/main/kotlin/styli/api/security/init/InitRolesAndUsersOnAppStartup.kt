package styli.api.security.init

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import styli.api.security.config.StyliConfig
import styli.api.security.model.Role
import styli.api.security.model.User
import styli.api.security.repository.RoleRepository
import styli.api.security.repository.UserRepository
import styli.api.security.service.UserService
import javax.transaction.Transactional

@Component
class InitRolesAndUsersOnAppStartup(
    private val userService: UserService,
    private val roleRepository: RoleRepository,
    private val styliConfig: StyliConfig,
) : ApplicationRunner {

    companion object {
        private val logger = LoggerFactory.getLogger(Companion::class.java)
    }

    @Transactional
    override fun run(args: ApplicationArguments?) {
        if (styliConfig.production == false) {
            val mapper = ObjectMapper()
            try {
                val roles: List<RoleDto> = mapper.readValue(ClassPathResource("init/roles.json").file)
                val users: List<UserDto> = mapper.readValue(ClassPathResource("init/users.json").file)
                roles.forEach { roleDto: RoleDto ->
                    roleRepository.save(Role(id = null, name = roleDto.name, description = roleDto.description))
                }
                users.forEach { userDto: UserDto ->
                    val userRoles: Set<Role> = userDto.roles.mapNotNull { roleName: String ->
                        return@mapNotNull roleRepository.findByName(roleName)
                    }.toSet()
                    val user = User(id = null, username = userDto.username, password = userDto.password)
                    user.roles.addAll(userRoles)
                    userService.createUser(user)
                }
            } catch (e: Exception) {
                logger.error(e.message.toString())
            }
        }
    }

    private data class RoleDto(var name: String = "", var description: String? = null)
    private data class UserDto(var username: String = "", var password: String = "", val roles: Set<String> = setOf())
}
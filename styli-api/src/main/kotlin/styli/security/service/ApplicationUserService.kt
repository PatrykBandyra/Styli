package styli.security.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import styli.profile.model.Profile
import styli.profile.repository.ProfileRepository
import styli.security.dto.request.UserRegistrationRequest
import styli.security.model.StyliAuthority
import styli.security.model.StyliUser
import styli.security.repository.StyliUserRepository

@Service
class ApplicationUserService(
    private val styliUserRepository: StyliUserRepository,
    private val profileRepository: ProfileRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val styliUser: StyliUser = styliUserRepository.findStyliUserByUsername(username)
            ?: throw UsernameNotFoundException("User with username $username not found")
        val authorities: Set<SimpleGrantedAuthority> = styliUser.authorities
            .map { authority: StyliAuthority -> SimpleGrantedAuthority(authority.name) }
            .toMutableSet()
        return User.builder()
            .username(styliUser.username)
            .password(styliUser.password)
            .authorities(authorities).build()
    }

    @Transactional
    fun registerUser(registrationRequest: UserRegistrationRequest): Profile {
        val profile = Profile(
            registrationRequest.name,
            registrationRequest.surname,
            registrationRequest.email,
            StyliUser(
                registrationRequest.username,
                passwordEncoder.encode(registrationRequest.password),
                mutableSetOf(StyliAuthority("READ"), StyliAuthority("CREATE"), StyliAuthority("DELETE"))
            )
        )
        return profileRepository.save(profile)
    }
}
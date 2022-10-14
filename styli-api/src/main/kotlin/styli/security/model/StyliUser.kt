package styli.security.model

import styli.profile.model.Profile
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.OneToOne
import javax.validation.constraints.NotBlank

@Entity
class StyliUser(
    @field:NotBlank(message = "Username cannot be blank")
    @Column(unique = true)
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String,

    @ManyToMany(mappedBy = "users", fetch = EAGER)
    val authorities: Set<StyliAuthority> = mutableSetOf(),

    @OneToOne(mappedBy = "user", fetch = LAZY)
    val profile: Profile? = null,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Int? = null,
)

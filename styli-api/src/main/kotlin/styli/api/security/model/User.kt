package styli.api.security.model

import javax.persistence.*
import javax.persistence.CascadeType.ALL
import javax.persistence.FetchType.EAGER
import javax.persistence.GenerationType.IDENTITY

@Entity
class User(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long?,
    @Column(unique = true)
    val username: String,
    val password: String,
    @ManyToMany(fetch = EAGER, cascade = [ALL])
    @JoinTable(
        name = "USER_ROLE",
        joinColumns = [JoinColumn(name = "USER_ID")],
        inverseJoinColumns = [JoinColumn(name = "ROLE_ID")]
    )
    val roles: MutableSet<Role> = mutableSetOf(),
)
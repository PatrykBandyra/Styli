package styli.security.model

import javax.persistence.CascadeType.PERSIST
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.validation.constraints.NotBlank

@Entity
class StyliAuthority(
    @field:NotBlank(message = "Styli authority name cannot be blank")
    @Column(unique = true)
    val name: String,

    @ManyToMany(cascade = [PERSIST], fetch = LAZY)
    @JoinTable(
        name = "authority_styliUser",
        joinColumns = [JoinColumn(name = "styliAuthority_id")],
        inverseJoinColumns = [JoinColumn(name = "styliUser_id")]
    )
    val users: Set<StyliUser> = mutableSetOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Int? = null,
)
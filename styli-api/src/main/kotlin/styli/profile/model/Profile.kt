package styli.profile.model

import styli.image.model.Image
import styli.security.model.StyliUser
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
class Profile(
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,

    @field:NotBlank(message = "Surname cannot be blank")
    val surname: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Invalid email formatting")
    @Column(unique = true)
    val email: String,

    @OneToOne(cascade = [ALL], fetch = EAGER)
    @JoinColumn(name = "styliUser_id", referencedColumnName = "id")
    val user: StyliUser,

    @OneToMany(mappedBy = "profile", cascade = [ALL], fetch = LAZY)
    val images: MutableList<Image> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null,
)
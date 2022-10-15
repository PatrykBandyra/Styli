package styli.image.model

import styli.profile.model.Profile
import javax.persistence.Basic
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.validation.constraints.NotEmpty

@Entity
class Image(
    @Lob @Basic(fetch = LAZY)
    @NotEmpty(message = "Image cannot be empty")
    val image: ByteArray,

    @ManyToOne(fetch = EAGER, optional = false)
    @JoinColumn(name = "profile_id")
    val profile: Profile,

    val description: String? = null,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null,
)
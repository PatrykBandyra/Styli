package styli.api.security.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Role(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long?,
    @NotBlank
    val name: String,
    val description: String?,
)
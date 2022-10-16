package styli.effects.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "styli")
data class EffectsConfig(
    val effects: List<Effect>,
) {
    data class Effect(
        val name: String,
        val url: String,
        val healthUrl: String,
    )
}

package styli.api.security.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "styli")
data class StyliConfig(
    var production: Boolean?,
    var secretKey: String?,
    var tokenValidityInSecs: Int?
)
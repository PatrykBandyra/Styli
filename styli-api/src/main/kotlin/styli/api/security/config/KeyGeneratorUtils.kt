package styli.api.security.config

import org.springframework.stereotype.Component
import java.security.KeyPair
import java.security.KeyPairGenerator




@Component
class KeyGeneratorUtils {
    companion object {
        fun generateRsaKey(): KeyPair? {
            val keyPair: KeyPair?
            try {
                val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
                keyPairGenerator.initialize(2048)
                keyPair = keyPairGenerator.generateKeyPair()
            } catch (e: Exception) {
                throw IllegalStateException()
            }
            return keyPair
        }
    }
}
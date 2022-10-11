package styli.api.security.config

import com.nimbusds.jose.jwk.RSAKey
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

class Jwks {
    companion object {
        fun generateRsa(): RSAKey {
            val keyPair: KeyPair? = KeyGeneratorUtils.generateRsaKey()
            val publicKey: RSAPublicKey = keyPair?.public as RSAPublicKey
            val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
            return RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build()
        }
    }
}
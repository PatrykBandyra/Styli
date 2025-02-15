package styli

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class StyliApiApplication

fun main(args: Array<String>) {
    runApplication<StyliApiApplication>(*args)
}

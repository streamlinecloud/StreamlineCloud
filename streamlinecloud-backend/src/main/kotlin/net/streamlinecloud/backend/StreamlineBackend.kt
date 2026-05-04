package net.streamlinecloud.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(
    basePackages = [
        "net.streamlinecloud.backend.entity",
        "net.streamlinecloud.api"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "net.streamlinecloud.backend.repository"
    ]
)
@SpringBootApplication(exclude = [
    UserDetailsServiceAutoConfiguration::class,
])
class StreamlinecloudBackendApplication

fun main(args: Array<String>) {
    runApplication<StreamlinecloudBackendApplication>(*args)
}
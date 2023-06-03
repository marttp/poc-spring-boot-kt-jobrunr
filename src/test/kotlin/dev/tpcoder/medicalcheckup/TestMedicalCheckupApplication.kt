package dev.tpcoder.medicalcheckup

import org.springframework.boot.SpringApplication
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@Configuration
class TestMedicalCheckupApplication {

    @Bean
    @ServiceConnection
    fun mySQLContainer(): MySQLContainer<*> = MySQLContainer(DockerImageName.parse("mysql:8.0.33"))
            .withUsername("root")
            .withPassword("root")
            .withDatabaseName("medical")
}

fun main(args: Array<String>) {
    SpringApplication
            .run(Application::class.java, *args)
            .apply { TestMedicalCheckupApplication::class.java }
}
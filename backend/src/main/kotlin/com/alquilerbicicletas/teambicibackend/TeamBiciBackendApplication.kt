package com.alquilerbicicletas.teambicibackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

/** Punto de entrada del backend de Team Bici (Spring Boot). */
@SpringBootApplication
@EnableScheduling
class TeamBiciBackendApplication

fun main(args: Array<String>) {
    runApplication<TeamBiciBackendApplication>(*args)
}

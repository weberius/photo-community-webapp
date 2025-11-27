package com.example.aicore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class AiCoreServiceApplication

fun main(args: Array<String>) {
    runApplication<AiCoreServiceApplication>(*args)
}

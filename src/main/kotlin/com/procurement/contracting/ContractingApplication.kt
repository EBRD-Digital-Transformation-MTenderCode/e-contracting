package com.procurement.contracting

import com.procurement.contracting.infrastructure.configuration.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
class ContractingApplication

fun main(args: Array<String>) {
    runApplication<ContractingApplication>(*args)
}
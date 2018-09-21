package com.procurement.contracting

import com.procurement.contracting.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
class ContractingApplication

fun main(args: Array<String>) {
    runApplication<ContractingApplication>(*args)
}
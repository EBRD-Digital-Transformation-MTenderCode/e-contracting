package com.procurement.contracting

import com.procurement.contracting.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
@EnableEurekaClient
class ContractingApplication

fun main(args: Array<String>) {
    runApplication<ContractingApplication>(*args)
}
package com.procurement.contracting.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.contracting.application.service",
        "com.procurement.contracting.infrastructure.handler"
    ]
)
class ServiceConfig

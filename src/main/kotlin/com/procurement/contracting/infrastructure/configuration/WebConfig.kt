package com.procurement.contracting.infrastructure.configuration

import com.procurement.contracting.infrastructure.configuration.properties.UriProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
@ComponentScan(
    basePackages = [
        "com.procurement.contracting.infrastructure.web.controller"
    ]
)

@EnableConfigurationProperties(value = [UriProperties::class])

class WebConfig

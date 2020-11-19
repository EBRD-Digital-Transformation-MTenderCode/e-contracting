package com.procurement.contracting.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    DaoConfiguration::class, 
    LoggerConfiguration::class,
    ObjectMapperConfig::class, 
    ServiceConfig::class, 
    TransformConfiguration::class,
    WebConfig::class
)
class ApplicationConfig

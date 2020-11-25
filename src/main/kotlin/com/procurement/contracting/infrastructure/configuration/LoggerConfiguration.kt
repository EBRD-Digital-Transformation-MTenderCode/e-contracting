package com.procurement.contracting.infrastructure.configuration

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.service.CustomLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {

    @Bean
    fun logger(): Logger = CustomLogger()
}
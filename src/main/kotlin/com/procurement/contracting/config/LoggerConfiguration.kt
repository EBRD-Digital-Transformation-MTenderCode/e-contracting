package com.procurement.contracting.config

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.service.logger.CustomLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {

    @Bean
    fun logger(): Logger = CustomLogger()
}
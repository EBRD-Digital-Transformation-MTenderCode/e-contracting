package com.procurement.contracting.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.service.JacksonJsonTransform
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TransformConfiguration(private val objectMapper: ObjectMapper) {

    @Bean
    fun transform(): Transform = JacksonJsonTransform(mapper = objectMapper)
}

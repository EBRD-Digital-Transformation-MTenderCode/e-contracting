package com.procurement.contracting.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.contracting.utils.DateUtil;
import com.procurement.contracting.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(basePackages = "com.procurement.contracting.service")
public class ServiceConfig {
    @Bean
    public JsonUtil jsonUtil() {
        return new JsonUtil(objectMapper());
    }

    @Bean
    public DateUtil dateUtil() {
        return new DateUtil();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper jackson2ObjectMapper = new ObjectMapper();
        jackson2ObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return jackson2ObjectMapper;
    }
}

package com.procurement.contracting.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@ComponentScan(basePackages = "com.procurement.contracting.model.entity")
@EnableCassandraRepositories(basePackages = "com.procurement.contracting.repository")
public class DatabaseConfig {
}

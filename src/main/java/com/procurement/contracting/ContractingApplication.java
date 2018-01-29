package com.procurement.contracting;

import com.procurement.contracting.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(scanBasePackageClasses = {ApplicationConfig.class},
    exclude = {LiquibaseAutoConfiguration.class})
public class ContractingApplication
{
    public static void main(final String[] args) {
        SpringApplication.run(ContractingApplication.class, args);
    }
}

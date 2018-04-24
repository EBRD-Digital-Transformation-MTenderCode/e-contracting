package com.procurement.contracting;

import com.procurement.contracting.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackageClasses = ApplicationConfig.class)
@EnableEurekaClient
public class ContractingApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ContractingApplication.class, args);
    }
}

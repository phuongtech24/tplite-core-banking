package com.tplite.core_banking.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI coreBankingOpenApi() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name(BEARER_AUTH)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("TPLite Core Banking Demo API")
                        .version("v1")
                        .description("Demo API for learning Java Spring Boot, Spring Security, JPA, transactions, Kafka, and modular backend design."))
                .components(new Components().addSecuritySchemes(BEARER_AUTH, securityScheme))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}

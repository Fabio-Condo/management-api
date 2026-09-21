package org.fcb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI managementPlatformOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Management Platform API")
                        .version("1.0.0")
                        .description(
                                "REST API for managing customers, accounts and transactions."
                        )
                        .contact(new Contact()
                                .name("FCB Management Platform")
                        )
                );
    }
}
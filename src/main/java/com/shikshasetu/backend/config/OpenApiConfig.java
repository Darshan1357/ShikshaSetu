package com.shikshasetu.backend.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shikshaSetuOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ShikshaSetu API")
                .version("1.0")
                .description("📚 Affordable Online Learning - ₹1/week!")
                .contact(new Contact()
                    .name("Team ShikshaSetu")
                    .email("support@shikshasetu.com")
                )
            );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("shikshasetu-public")
            .pathsToMatch("/api/**")
            .build();
    }
}

package com.vanna.orders_serviceApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RestOrders API")
                        .version("1.0.0")
                        .description("""
                                Order management microservice with JWT authentication

                                ## Key Features:
                                - User registration and authentication
                                - Order management (CRUD operations)
                                - Role-based authorization (USER/ADMIN)

                                ## How to use:
                                1. Register via POST /api/auth/register
                                2. Obtain JWT token via POST /api/auth/login
                                3. Click "Authorize" button and paste the token
                                4. Now you can test protected endpoints!
                                """)
                        .contact(new Contact()
                                .name("RestOrders Team")
                                .email("support@restorders.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token obtained from login")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
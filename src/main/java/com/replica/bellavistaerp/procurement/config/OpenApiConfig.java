package com.replica.bellavistaerp.procurement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bellaVistaOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Bella Vista College ERP API")
                        .description("""
                                Centralised Enterprise Resource Planning API
                                for Bella Vista College.

                                Modules include:
                                - Procurement Management
                                - Stores and Inventory Management
                                - Item Requisitions
                                - Approvals and Workflow
                                - Stock Transfers
                                - Kitchen Pantry Management
                                - Uniforms Management
                                """)
                        .version("1.0.0"));
    }
}
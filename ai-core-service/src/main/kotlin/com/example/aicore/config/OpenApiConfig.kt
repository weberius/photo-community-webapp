package com.example.aicore.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(Info()
                .title("AI Core Service API")
                .version("1.0")
                .description("""
                    Central AI orchestration service providing unified interface to AI providers (Gemini/GPT).
                    
                    Features:
                    - Execute AI pipelines for photo analysis, portfolio review, and scouting
                    - Manage prompt templates
                    - Track token usage and costs
                """.trimIndent())
                .contact(Contact()
                    .name("AI Core Service Team")
                    .email("support@example.com")
                )
            )
    }
}

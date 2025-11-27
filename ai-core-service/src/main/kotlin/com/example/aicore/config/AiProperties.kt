package com.example.aicore.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "ai")
class AiProperties {
    var defaultProvider: String = "gemini"
    var gemini: GeminiConfig = GeminiConfig()
    var openai: OpenAiConfig = OpenAiConfig()
}

class GeminiConfig {
    var apiKey: String = ""
    var apiUrl: String = ""
    var timeoutSeconds: Long = 60
}

class OpenAiConfig {
    var apiKey: String = ""
    var apiUrl: String = ""
    var model: String = "gpt-4"
    var timeoutSeconds: Long = 60
}


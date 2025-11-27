package com.example.aicore.adapter

import com.example.aicore.config.AiProperties
import com.example.aicore.exception.AiProviderException
import org.springframework.stereotype.Component

@Component
class AiClientFactory(
    private val aiProperties: AiProperties,
    private val geminiClient: GeminiClient,
    private val openAiClient: OpenAiClient
) {
    
    fun getClient(provider: String? = null): AiModelClient {
        val selectedProvider = provider ?: aiProperties.defaultProvider
        
        return when (selectedProvider.lowercase()) {
            "gemini" -> geminiClient
            "openai" -> openAiClient
            else -> throw AiProviderException("Unknown AI provider: $selectedProvider")
        }
    }
    
    fun getDefaultClient(): AiModelClient {
        return getClient(aiProperties.defaultProvider)
    }
}

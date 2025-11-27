package com.example.aicore.adapter

import com.example.aicore.adapter.model.AiRequest
import com.example.aicore.adapter.model.AiResponse

/**
 * Interface for AI model providers.
 * Implementations should handle communication with specific AI providers (Gemini, OpenAI, etc.)
 */
interface AiModelClient {
    
    /**
     * Generate AI response for the given request
     * @param request The AI request with prompt and parameters
     * @return AI response with generated content and metadata
     */
    suspend fun generate(request: AiRequest): AiResponse
    
    /**
     * Get the provider name (e.g., "gemini", "openai")
     */
    fun getProviderName(): String
}

package com.example.aicore.adapter

import com.example.aicore.config.AiProperties
import com.example.aicore.exception.AiProviderException
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AiClientFactoryTest {
    
    private lateinit var aiProperties: AiProperties
    private lateinit var geminiClient: GeminiClient
    private lateinit var openAiClient: OpenAiClient
    private lateinit var factory: AiClientFactory
    
    @BeforeEach
    fun setUp() {
        aiProperties = mockk()
        geminiClient = mockk()
        openAiClient = mockk()
        
        every { geminiClient.getProviderName() } returns "gemini"
        every { openAiClient.getProviderName() } returns "openai"
        
        factory = AiClientFactory(aiProperties, geminiClient, openAiClient)
    }
    
    @Test
    fun `should return Gemini client when provider is gemini`() {
        // When
        val client = factory.getClient("gemini")
        
        // Then
        assertThat(client).isEqualTo(geminiClient)
        assertThat(client.getProviderName()).isEqualTo("gemini")
    }
    
    @Test
    fun `should return OpenAI client when provider is openai`() {
        // When
        val client = factory.getClient("openai")
        
        // Then
        assertThat(client).isEqualTo(openAiClient)
        assertThat(client.getProviderName()).isEqualTo("openai")
    }
    
    @Test
    fun `should return Gemini client when provider is GEMINI (case insensitive)`() {
        // When
        val client = factory.getClient("GEMINI")
        
        // Then
        assertThat(client).isEqualTo(geminiClient)
    }
    
    @Test
    fun `should return default client when provider is null`() {
        // Given
        every { aiProperties.defaultProvider } returns "gemini"
        
        // When
        val client = factory.getClient(null)
        
        // Then
        assertThat(client).isEqualTo(geminiClient)
    }
    
    @Test
    fun `should return default client when using getDefaultClient`() {
        // Given
        every { aiProperties.defaultProvider } returns "openai"
        
        // When
        val client = factory.getDefaultClient()
        
        // Then
        assertThat(client).isEqualTo(openAiClient)
    }
    
    @Test
    fun `should throw exception for unknown provider`() {
        // When & Then
        assertThatThrownBy {
            factory.getClient("unknown-provider")
        }.isInstanceOf(AiProviderException::class.java)
            .hasMessageContaining("Unknown AI provider: unknown-provider")
    }
}

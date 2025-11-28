package com.example.aicore.adapter

import com.example.aicore.TestDataBuilder
import com.example.aicore.config.AiProperties
import com.example.aicore.config.GeminiConfig
import com.example.aicore.config.OpenAiConfig
import com.example.aicore.exception.AiProviderException
import com.example.aicore.exception.RateLimitException
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OpenAiClientTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var openAiClient: OpenAiClient
    private lateinit var aiProperties: AiProperties
    private val objectMapper = ObjectMapper()
    
    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        aiProperties = AiProperties().apply {
            defaultProvider = "openai"
            gemini = GeminiConfig().apply {
                apiKey = ""
                apiUrl = ""
                timeoutSeconds = 30
            }
            openai = OpenAiConfig().apply {
                apiKey = "test-api-key"
                apiUrl = mockWebServer.url("/").toString().trimEnd('/')
                model = "gpt-4"
                timeoutSeconds = 30
            }
        }
        
        openAiClient = OpenAiClient(aiProperties, objectMapper)
    }
    
    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `should successfully call OpenAI API with text prompt`() = runBlocking {
        // Given
        val request = TestDataBuilder.buildAiRequest(binaryData = null)
        val mockResponse = """
            {
                "choices": [{
                    "message": {
                        "content": "This is a test response"
                    },
                    "finish_reason": "stop"
                }],
                "usage": {
                    "total_tokens": 1234
                },
                "model": "gpt-4"
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When
        val response = openAiClient.generate(request)
        
        // Then
        assertThat(response.content).isEqualTo("This is a test response")
        assertThat(response.tokenCount).isEqualTo(1234)
        assertThat(response.model).isEqualTo("gpt-4")
        assertThat(response.finishReason).isEqualTo("stop")
        
        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("Bearer test-api-key")
    }
    
    @Test
    fun `should successfully call OpenAI API with image data`() = runBlocking {
        // Given
        val request = TestDataBuilder.buildAiRequest(binaryData = "base64encodedimage")
        val mockResponse = """
            {
                "choices": [{
                    "message": {
                        "content": "Image analysis result"
                    },
                    "finish_reason": "stop"
                }],
                "usage": {
                    "total_tokens": 2000
                },
                "model": "gpt-4-vision"
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When
        val response = openAiClient.generate(request)
        
        // Then
        assertThat(response.content).isEqualTo("Image analysis result")
        assertThat(response.tokenCount).isEqualTo(2000)
        
        val recordedRequest = mockWebServer.takeRequest()
        val requestBody = recordedRequest.body.readUtf8()
        assertThat(requestBody).contains("image_url")
        assertThat(requestBody).contains("data:image/jpeg;base64,base64encodedimage")
    }
    
    @Test
    fun `should throw RateLimitException on 429 response`() {
        // Given
        val request = TestDataBuilder.buildAiRequest()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody("Rate limit exceeded")
        )
        
        // When & Then
        assertThatThrownBy {
            runBlocking { openAiClient.generate(request) }
        }.isInstanceOf(RateLimitException::class.java)
            .hasMessageContaining("rate limit")
    }
    
    @Test
    fun `should throw AiProviderException on error response`() {
        // Given
        val request = TestDataBuilder.buildAiRequest()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("Bad request")
        )
        
        // When & Then
        assertThatThrownBy {
            runBlocking { openAiClient.generate(request) }
        }.isInstanceOf(AiProviderException::class.java)
            .hasMessageContaining("OpenAI API error")
    }
    
    @Test
    fun `should throw AiProviderException when API key is blank`() {
        // Given
        aiProperties.openai.apiKey = ""
        val request = TestDataBuilder.buildAiRequest()
        
        // When & Then
        assertThatThrownBy {
            runBlocking { openAiClient.generate(request) }
        }.isInstanceOf(AiProviderException::class.java)
            .hasMessageContaining("API key not configured")
    }
    
    @Test
    fun `should return provider name`() {
        assertThat(openAiClient.getProviderName()).isEqualTo("openai")
    }
}

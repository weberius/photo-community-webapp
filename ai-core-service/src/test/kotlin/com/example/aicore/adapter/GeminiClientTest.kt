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

class GeminiClientTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var geminiClient: GeminiClient
    private lateinit var aiProperties: AiProperties
    private val objectMapper = ObjectMapper()
    
    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        aiProperties = AiProperties().apply {
            defaultProvider = "gemini"
            gemini = GeminiConfig().apply {
                apiKey = "test-api-key"
                apiUrl = mockWebServer.url("/").toString().trimEnd('/')
                timeoutSeconds = 30
            }
            openai = OpenAiConfig().apply {
                apiKey = ""
                apiUrl = ""
                model = "gpt-4"
                timeoutSeconds = 30
            }
        }
        
        geminiClient = GeminiClient(aiProperties, objectMapper)
    }
    
    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `should successfully call Gemini API with text prompt`() = runBlocking {
        // Given
        val request = TestDataBuilder.buildAiRequest(binaryData = null)
        val mockResponse = """
            {
                "candidates": [{
                    "content": {
                        "parts": [{
                            "text": "This is a test response"
                        }]
                    },
                    "finishReason": "STOP"
                }],
                "usageMetadata": {
                    "totalTokenCount": 1234
                }
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When
        val response = geminiClient.generate(request)
        
        // Then
        assertThat(response.content).isEqualTo("This is a test response")
        assertThat(response.tokenCount).isEqualTo(1234)
        assertThat(response.model).isEqualTo("gemini-pro")
        assertThat(response.finishReason).isEqualTo("STOP")
        
        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.path).contains("key=test-api-key")
    }
    
    @Test
    fun `should successfully call Gemini API with image data`() = runBlocking {
        // Given
        val request = TestDataBuilder.buildAiRequest(binaryData = "base64encodedimage")
        val mockResponse = """
            {
                "candidates": [{
                    "content": {
                        "parts": [{
                            "text": "Image analysis result"
                        }]
                    }
                }],
                "usageMetadata": {
                    "totalTokenCount": 2000
                }
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When
        val response = geminiClient.generate(request)
        
        // Then
        assertThat(response.content).isEqualTo("Image analysis result")
        assertThat(response.tokenCount).isEqualTo(2000)
        
        val recordedRequest = mockWebServer.takeRequest()
        val requestBody = recordedRequest.body.readUtf8()
        assertThat(requestBody).contains("inline_data")
        assertThat(requestBody).contains("base64encodedimage")
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
            runBlocking { geminiClient.generate(request) }
        }.isInstanceOf(RateLimitException::class.java)
            .hasMessageContaining("rate limit")
    }
    
    @Test
    fun `should throw AiProviderException on 4xx error`() {
        // Given
        val request = TestDataBuilder.buildAiRequest()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("Bad request")
        )
        
        // When & Then
        assertThatThrownBy {
            runBlocking { geminiClient.generate(request) }
        }.isInstanceOf(AiProviderException::class.java)
            .hasMessageContaining("Gemini API error")
    }
    
    @Test
    fun `should throw AiProviderException on 5xx error`() {
        // Given
        val request = TestDataBuilder.buildAiRequest()
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal server error")
        )
        
        // When & Then
        assertThatThrownBy {
            runBlocking { geminiClient.generate(request) }
        }.isInstanceOf(AiProviderException::class.java)
    }
    
    @Test
    fun `should throw AiProviderException when API key is blank`() {
        // Given
        aiProperties.gemini.apiKey = ""
        val request = TestDataBuilder.buildAiRequest()
        
        // When & Then
        assertThatThrownBy {
            runBlocking { geminiClient.generate(request) }
        }.isInstanceOf(AiProviderException::class.java)
            .hasMessageContaining("API key not configured")
    }
    
    @Test
    fun `should throw AiProviderException when response has no content`() {
        // Given
        val request = TestDataBuilder.buildAiRequest()
        val mockResponse = """
            {
                "candidates": []
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )
        
        // When & Then
        assertThatThrownBy {
            runBlocking { geminiClient.generate(request) }
        }.isInstanceOf(AiProviderException::class.java)
            .hasMessageContaining("No content")
    }
    
    @Test
    fun `should return provider name`() {
        assertThat(geminiClient.getProviderName()).isEqualTo("gemini")
    }
}

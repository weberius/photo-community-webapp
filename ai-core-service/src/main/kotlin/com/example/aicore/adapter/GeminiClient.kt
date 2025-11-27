package com.example.aicore.adapter

import com.example.aicore.adapter.model.AiRequest
import com.example.aicore.adapter.model.AiResponse
import com.example.aicore.config.AiProperties
import com.example.aicore.exception.AiProviderException
import com.example.aicore.exception.ModelTimeoutException
import com.example.aicore.exception.RateLimitException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class GeminiClient(
    private val aiProperties: AiProperties,
    private val objectMapper: ObjectMapper
) : AiModelClient {
    
    private val logger = LoggerFactory.getLogger(GeminiClient::class.java)
    
    private val webClient = WebClient.builder()
        .baseUrl(aiProperties.gemini.apiUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()
    
    override suspend fun generate(request: AiRequest): AiResponse {
        if (aiProperties.gemini.apiKey.isBlank()) {
            throw AiProviderException("Gemini API key not configured")
        }
        
        try {
            logger.debug("Sending request to Gemini API")
            
            val requestBody = buildGeminiRequest(request)
            
            val response = webClient.post()
                .uri { uriBuilder ->
                    uriBuilder
                        .queryParam("key", aiProperties.gemini.apiKey)
                        .build()
                }
                .bodyValue(requestBody)
                .retrieve()
                .onStatus({ it.value() == 429 }) { 
                    Mono.error(RateLimitException("Gemini API rate limit exceeded"))
                }
                .onStatus({ it.is4xxClientError || it.is5xxServerError }) { response ->
                    response.bodyToMono(String::class.java).flatMap { body ->
                        Mono.error(AiProviderException("Gemini API error: $body"))
                    }
                }
                .awaitBody<String>()
            
            return parseGeminiResponse(response)
            
        } catch (e: RateLimitException) {
            throw e
        } catch (e: AiProviderException) {
            throw e
        } catch (e: Exception) {
            logger.error("Error calling Gemini API", e)
            throw AiProviderException("Failed to call Gemini API: ${e.message}", e)
        }
    }
    
    private fun buildGeminiRequest(request: AiRequest): Map<String, Any> {
        val parts = mutableListOf<Map<String, Any>>()
        
        // Add text prompt
        parts.add(mapOf("text" to request.prompt))
        
        // Add image if present
        if (!request.binaryData.isNullOrBlank()) {
            parts.add(mapOf(
                "inline_data" to mapOf(
                    "mime_type" to "image/jpeg",
                    "data" to request.binaryData
                )
            ))
        }
        
        return mapOf(
            "contents" to listOf(
                mapOf("parts" to parts)
            ),
            "generationConfig" to mapOf(
                "temperature" to request.temperature,
                "maxOutputTokens" to request.maxTokens
            )
        )
    }
    
    private fun parseGeminiResponse(responseBody: String): AiResponse {
        val jsonNode: JsonNode = objectMapper.readTree(responseBody)
        
        val content = jsonNode
            .path("candidates")
            .firstOrNull()
            ?.path("content")
            ?.path("parts")
            ?.firstOrNull()
            ?.path("text")
            ?.asText()
            ?: throw AiProviderException("No content in Gemini response")
        
        val tokenCount = jsonNode
            .path("usageMetadata")
            .path("totalTokenCount")
            .asInt(0)
        
        val finishReason = jsonNode
            .path("candidates")
            .firstOrNull()
            ?.path("finishReason")
            ?.asText()
        
        return AiResponse(
            content = content,
            tokenCount = tokenCount,
            model = "gemini-pro",
            finishReason = finishReason
        )
    }
    
    override fun getProviderName(): String = "gemini"
}

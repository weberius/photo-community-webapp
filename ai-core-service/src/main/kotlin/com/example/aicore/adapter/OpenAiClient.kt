package com.example.aicore.adapter

import com.example.aicore.adapter.model.AiRequest
import com.example.aicore.adapter.model.AiResponse
import com.example.aicore.config.AiProperties
import com.example.aicore.exception.AiProviderException
import com.example.aicore.exception.RateLimitException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

@Component
class OpenAiClient(
    private val aiProperties: AiProperties,
    private val objectMapper: ObjectMapper
) : AiModelClient {
    
    private val logger = LoggerFactory.getLogger(OpenAiClient::class.java)
    
    private val webClient = WebClient.builder()
        .baseUrl(aiProperties.openai.apiUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()
    
    override suspend fun generate(request: AiRequest): AiResponse {
        if (aiProperties.openai.apiKey.isBlank()) {
            throw AiProviderException("OpenAI API key not configured")
        }
        
        try {
            logger.debug("Sending request to OpenAI API")
            
            val requestBody = buildOpenAiRequest(request)
            
            val response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${aiProperties.openai.apiKey}")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus({ it.value() == 429 }) {
                    Mono.error(RateLimitException("OpenAI API rate limit exceeded"))
                }
                .onStatus({ it.is4xxClientError || it.is5xxServerError }) { response ->
                    response.bodyToMono(String::class.java).flatMap { body ->
                        Mono.error(AiProviderException("OpenAI API error: $body"))
                    }
                }
                .awaitBody<String>()
            
            return parseOpenAiResponse(response)
            
        } catch (e: RateLimitException) {
            throw e
        } catch (e: AiProviderException) {
            throw e
        } catch (e: Exception) {
            logger.error("Error calling OpenAI API", e)
            throw AiProviderException("Failed to call OpenAI API: ${e.message}", e)
        }
    }
    
    private fun buildOpenAiRequest(request: AiRequest): Map<String, Any> {
        val messages = mutableListOf<Map<String, Any>>()
        
        if (!request.binaryData.isNullOrBlank()) {
            // GPT-4 Vision request
            messages.add(mapOf(
                "role" to "user",
                "content" to listOf(
                    mapOf("type" to "text", "text" to request.prompt),
                    mapOf(
                        "type" to "image_url",
                        "image_url" to mapOf(
                            "url" to "data:image/jpeg;base64,${request.binaryData}"
                        )
                    )
                )
            ))
        } else {
            // Standard text request
            messages.add(mapOf(
                "role" to "user",
                "content" to request.prompt
            ))
        }
        
        return mapOf(
            "model" to (request.model ?: aiProperties.openai.model),
            "messages" to messages,
            "temperature" to request.temperature,
            "max_tokens" to request.maxTokens
        )
    }
    
    private fun parseOpenAiResponse(responseBody: String): AiResponse {
        val jsonNode: JsonNode = objectMapper.readTree(responseBody)
        
        val content = jsonNode
            .path("choices")
            .firstOrNull()
            ?.path("message")
            ?.path("content")
            ?.asText()
            ?: throw AiProviderException("No content in OpenAI response")
        
        val tokenCount = jsonNode
            .path("usage")
            .path("total_tokens")
            .asInt(0)
        
        val model = jsonNode
            .path("model")
            .asText("unknown")
        
        val finishReason = jsonNode
            .path("choices")
            .firstOrNull()
            ?.path("finish_reason")
            ?.asText()
        
        return AiResponse(
            content = content,
            tokenCount = tokenCount,
            model = model,
            finishReason = finishReason
        )
    }
    
    override fun getProviderName(): String = "openai"
}

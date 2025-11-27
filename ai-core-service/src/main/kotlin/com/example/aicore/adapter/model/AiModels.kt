package com.example.aicore.adapter.model

data class AiRequest(
    val prompt: String,
    val binaryData: String? = null,  // Base64 encoded
    val temperature: Double = 0.7,
    val maxTokens: Int = 2048,
    val model: String? = null
)

data class AiResponse(
    val content: String,
    val tokenCount: Int,
    val model: String,
    val finishReason: String? = null
)

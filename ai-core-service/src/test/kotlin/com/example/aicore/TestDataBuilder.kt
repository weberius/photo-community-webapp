package com.example.aicore

import com.example.aicore.adapter.model.AiRequest
import com.example.aicore.adapter.model.AiResponse
import com.example.aicore.controller.dto.AiExecuteRequest
import com.example.aicore.controller.dto.AiExecuteResponse
import com.example.aicore.persistence.entity.AiCost
import com.example.aicore.persistence.entity.AiLog
import com.example.aicore.persistence.entity.PromptTemplate
import com.example.aicore.pipeline.model.AiExecutionContext
import com.example.aicore.pipeline.model.AiExecutionResult
import com.example.aicore.pipeline.model.TaskType
import com.example.aicore.templates.TemplateModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Test data builder for creating test objects
 */
object TestDataBuilder {
    
    fun buildAiExecuteRequest(
        taskType: TaskType = TaskType.PHOTO_ANALYSIS,
        promptParams: Map<String, Any> = mapOf("photographerName" to "Test", "genre" to "Portrait"),
        binaryInput: String? = "base64encodedimage",
        provider: String? = "gemini"
    ) = AiExecuteRequest(
        taskType = taskType,
        promptParams = promptParams,
        binaryInput = binaryInput,
        provider = provider
    )
    
    fun buildAiExecuteResponse(
        success: Boolean = true,
        content: String? = """{"overallScore": 85}""",
        tokenCount: Int = 1234,
        provider: String = "gemini",
        errorMessage: String? = null
    ) = AiExecuteResponse(
        success = success,
        content = content,
        tokenCount = tokenCount,
        provider = provider,
        errorMessage = errorMessage
    )
    
    fun buildAiExecutionContext(
        taskType: TaskType = TaskType.PHOTO_ANALYSIS,
        promptParams: Map<String, Any> = mapOf("photographerName" to "Test", "genre" to "Portrait"),
        binaryInput: String? = "base64encodedimage",
        provider: String? = "gemini"
    ) = AiExecutionContext(
        taskType = taskType,
        promptParams = promptParams,
        binaryInput = binaryInput,
        provider = provider
    )
    
    fun buildAiExecutionResult(
        success: Boolean = true,
        content: String? = """{"overallScore": 85}""",
        tokenCount: Int = 1234,
        provider: String = "gemini",
        errorMessage: String? = null
    ) = AiExecutionResult(
        success = success,
        content = content,
        tokenCount = tokenCount,
        provider = provider,
        errorMessage = errorMessage
    )
    
    fun buildAiRequest(
        prompt: String = "Analyze this photo",
        binaryData: String? = "base64encodedimage",
        temperature: Double = 0.7,
        maxTokens: Int = 2048
    ) = AiRequest(
        prompt = prompt,
        binaryData = binaryData,
        temperature = temperature,
        maxTokens = maxTokens
    )
    
    fun buildAiResponse(
        content: String = """{"overallScore": 85}""",
        tokenCount: Int = 1234,
        model: String = "gemini-pro",
        finishReason: String? = "STOP"
    ) = AiResponse(
        content = content,
        tokenCount = tokenCount,
        model = model,
        finishReason = finishReason
    )
    
    fun buildAiLog(
        id: UUID = UUID.randomUUID(),
        taskType: String = "PHOTO_ANALYSIS",
        requestTimestamp: LocalDateTime = LocalDateTime.now(),
        responseTimestamp: LocalDateTime? = LocalDateTime.now(),
        provider: String = "gemini",
        success: Boolean = true,
        tokenCount: Int = 1234,
        errorMessage: String? = null
    ) = AiLog(
        id = id,
        taskType = taskType,
        requestTimestamp = requestTimestamp,
        responseTimestamp = responseTimestamp,
        provider = provider,
        success = success,
        tokenCount = tokenCount,
        errorMessage = errorMessage
    )
    
    fun buildAiCost(
        id: UUID = UUID.randomUUID(),
        date: LocalDate = LocalDate.now(),
        provider: String = "gemini",
        taskType: String = "PHOTO_ANALYSIS",
        totalTokens: Int = 5000,
        requestCount: Int = 5,
        estimatedCost: BigDecimal = BigDecimal("0.0025")
    ) = AiCost(
        id = id,
        date = date,
        provider = provider,
        taskType = taskType,
        totalTokens = totalTokens,
        requestCount = requestCount,
        estimatedCost = estimatedCost
    )
    
    fun buildPromptTemplate(
        id: UUID = UUID.randomUUID(),
        name: String = "test-template",
        version: String = "1.0",
        content: String = "Test prompt with {{param}}",
        parameters: String? = null,
        active: Boolean = true
    ) = PromptTemplate(
        id = id,
        name = name,
        version = version,
        content = content,
        parameters = parameters,
        active = active
    )
    
    fun buildTemplateModel(
        name: String = "test-template",
        version: String = "1.0",
        description: String = "Test template",
        parameters: List<String> = listOf("param"),
        content: String = "Test prompt with {{param}}"
    ) = TemplateModel(
        name = name,
        version = version,
        description = description,
        parameters = parameters,
        content = content
    )
}

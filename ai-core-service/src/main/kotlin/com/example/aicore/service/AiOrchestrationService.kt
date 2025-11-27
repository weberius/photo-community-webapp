package com.example.aicore.service

import com.example.aicore.persistence.entity.AiCost
import com.example.aicore.persistence.entity.AiLog
import com.example.aicore.persistence.repository.AiCostRepository
import com.example.aicore.persistence.repository.AiLogRepository
import com.example.aicore.pipeline.PipelineRegistry
import com.example.aicore.pipeline.model.AiExecutionContext
import com.example.aicore.pipeline.model.AiExecutionResult
import com.example.aicore.pipeline.model.TaskType
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class AiOrchestrationService(
    private val pipelineRegistry: PipelineRegistry,
    private val aiLogRepository: AiLogRepository,
    private val aiCostRepository: AiCostRepository
) {
    
    private val logger = LoggerFactory.getLogger(AiOrchestrationService::class.java)
    
    @Transactional
    fun executeAiTask(context: AiExecutionContext): AiExecutionResult {
        val logId = UUID.randomUUID()
        val requestTimestamp = LocalDateTime.now()
        
        // Create initial log entry
        val aiLog = AiLog(
            id = logId,
            taskType = context.taskType.name,
            requestTimestamp = requestTimestamp,
            provider = context.provider
        )
        aiLogRepository.save(aiLog)
        
        // Execute pipeline
        val result = runBlocking {
            try {
                val pipeline = pipelineRegistry.getPipeline(context.taskType)
                pipeline.execute(context)
            } catch (e: Exception) {
                logger.error("Pipeline execution failed", e)
                AiExecutionResult(
                    success = false,
                    provider = context.provider ?: "unknown",
                    errorMessage = e.message
                )
            }
        }
        
        // Update log with result
        aiLog.responseTimestamp = LocalDateTime.now()
        aiLog.success = result.success
        aiLog.tokenCount = result.tokenCount
        aiLog.errorMessage = result.errorMessage
        aiLog.provider = result.provider
        aiLogRepository.save(aiLog)
        
        // Track costs if successful
        if (result.success) {
            trackCost(
                date = LocalDate.now(),
                provider = result.provider,
                taskType = context.taskType.name,
                tokenCount = result.tokenCount
            )
        }
        
        return result
    }
    
    private fun trackCost(
        date: LocalDate,
        provider: String,
        taskType: String,
        tokenCount: Int
    ) {
        val existingCost = aiCostRepository.findByDateAndProviderAndTaskType(
            date, provider, taskType
        )
        
        if (existingCost != null) {
            // Update existing record
            existingCost.totalTokens += tokenCount
            existingCost.requestCount += 1
            existingCost.estimatedCost = calculateCost(existingCost.totalTokens, provider)
            existingCost.updatedAt = LocalDateTime.now()
            aiCostRepository.save(existingCost)
        } else {
            // Create new record
            val newCost = AiCost(
                date = date,
                provider = provider,
                taskType = taskType,
                totalTokens = tokenCount,
                requestCount = 1,
                estimatedCost = calculateCost(tokenCount, provider)
            )
            aiCostRepository.save(newCost)
        }
    }
    
    private fun calculateCost(tokens: Int, provider: String): BigDecimal {
        // Simple cost estimation (adjust rates as needed)
        val costPerThousandTokens = when (provider.lowercase()) {
            "gemini" -> BigDecimal("0.0005")  // $0.0005 per 1K tokens
            "openai" -> BigDecimal("0.03")     // $0.03 per 1K tokens (GPT-4)
            else -> BigDecimal.ZERO
        }
        
        return costPerThousandTokens
            .multiply(BigDecimal(tokens))
            .divide(BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP)
    }
}

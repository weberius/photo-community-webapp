package com.example.aicore.pipeline

import com.example.aicore.adapter.AiClientFactory
import com.example.aicore.adapter.model.AiRequest
import com.example.aicore.pipeline.model.AiExecutionContext
import com.example.aicore.pipeline.model.AiExecutionResult
import com.example.aicore.templates.TemplateRegistry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PortfolioReviewPipeline(
    private val templateRegistry: TemplateRegistry,
    private val aiClientFactory: AiClientFactory
) : AiPipeline {
    
    private val logger = LoggerFactory.getLogger(PortfolioReviewPipeline::class.java)
    
    override suspend fun execute(context: AiExecutionContext): AiExecutionResult {
        return try {
            logger.info("Executing portfolio review pipeline")
            
            val prompt = templateRegistry.renderTemplate(
                getTemplateName(),
                context.promptParams
            )
            
            val client = aiClientFactory.getClient(context.provider)
            
            val aiRequest = AiRequest(
                prompt = prompt,
                binaryData = context.binaryInput,
                temperature = 0.7,
                maxTokens = 3000
            )
            
            val response = client.generate(aiRequest)
            
            logger.info("Portfolio review completed successfully, tokens: ${response.tokenCount}")
            
            AiExecutionResult(
                success = true,
                content = response.content,
                tokenCount = response.tokenCount,
                provider = client.getProviderName()
            )
            
        } catch (e: Exception) {
            logger.error("Portfolio review pipeline failed", e)
            AiExecutionResult(
                success = false,
                provider = context.provider ?: "unknown",
                errorMessage = e.message
            )
        }
    }
    
    override fun getTemplateName(): String = "portfolio-review"
}

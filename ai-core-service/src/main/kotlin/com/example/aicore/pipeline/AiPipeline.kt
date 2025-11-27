package com.example.aicore.pipeline

import com.example.aicore.pipeline.model.AiExecutionContext
import com.example.aicore.pipeline.model.AiExecutionResult

/**
 * Interface for AI task pipelines.
 * Each pipeline handles a specific type of AI task.
 */
interface AiPipeline {
    
    /**
     * Execute the pipeline with the given context
     * @param context Execution context with task parameters
     * @return Execution result with AI response
     */
    suspend fun execute(context: AiExecutionContext): AiExecutionResult
    
    /**
     * Get the template name used by this pipeline
     */
    fun getTemplateName(): String
}

package com.example.aicore.controller

import com.example.aicore.controller.dto.AiExecuteRequest
import com.example.aicore.controller.dto.AiExecuteResponse
import com.example.aicore.pipeline.model.AiExecutionContext
import com.example.aicore.service.AiOrchestrationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Execution", description = "Execute AI pipelines")
class AiController(
    private val aiOrchestrationService: AiOrchestrationService
) {
    
    @PostMapping("/execute")
    @Operation(
        summary = "Execute AI pipeline",
        description = "Execute an AI task pipeline with the specified parameters"
    )
    fun executeAiTask(
        @RequestBody request: AiExecuteRequest
    ): ResponseEntity<AiExecuteResponse> {
        
        val context = AiExecutionContext(
            taskType = request.taskType,
            promptParams = request.promptParams,
            binaryInput = request.binaryInput,
            provider = request.provider
        )
        
        val result = aiOrchestrationService.executeAiTask(context)
        
        val response = AiExecuteResponse(
            success = result.success,
            content = result.content,
            tokenCount = result.tokenCount,
            provider = result.provider,
            errorMessage = result.errorMessage
        )
        
        return ResponseEntity.ok(response)
    }
}

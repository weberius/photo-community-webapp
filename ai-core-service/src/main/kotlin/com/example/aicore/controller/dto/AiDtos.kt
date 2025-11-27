package com.example.aicore.controller.dto

import com.example.aicore.pipeline.model.TaskType

data class AiExecuteRequest(
    val taskType: TaskType,
    val promptParams: Map<String, Any>,
    val binaryInput: String? = null,
    val provider: String? = null
)

data class AiExecuteResponse(
    val success: Boolean,
    val content: String? = null,
    val tokenCount: Int = 0,
    val provider: String,
    val errorMessage: String? = null
)

package com.example.aicore.pipeline.model

enum class TaskType {
    PHOTO_ANALYSIS,
    PORTFOLIO_REVIEW,
    SCOUTING
}

data class AiExecutionContext(
    val taskType: TaskType,
    val promptParams: Map<String, Any>,
    val binaryInput: String? = null,
    val provider: String? = null
)

data class AiExecutionResult(
    val success: Boolean,
    val content: String? = null,
    val tokenCount: Int = 0,
    val provider: String,
    val errorMessage: String? = null
)

package com.example.aicore.pipeline

import com.example.aicore.exception.InvalidPipelineException
import com.example.aicore.pipeline.model.TaskType
import org.springframework.stereotype.Component

@Component
class PipelineRegistry(
    private val photoAnalysisPipeline: PhotoAnalysisPipeline,
    private val portfolioReviewPipeline: PortfolioReviewPipeline,
    private val scoutingPipeline: ScoutingPipeline
) {
    
    private val pipelines: Map<TaskType, AiPipeline> = mapOf(
        TaskType.PHOTO_ANALYSIS to photoAnalysisPipeline,
        TaskType.PORTFOLIO_REVIEW to portfolioReviewPipeline,
        TaskType.SCOUTING to scoutingPipeline
    )
    
    fun getPipeline(taskType: TaskType): AiPipeline {
        return pipelines[taskType] 
            ?: throw InvalidPipelineException("No pipeline found for task type: $taskType")
    }
    
    fun getAllTaskTypes(): List<TaskType> {
        return pipelines.keys.toList()
    }
}

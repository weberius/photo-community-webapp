package com.example.aicore.pipeline

import com.example.aicore.exception.InvalidPipelineException
import com.example.aicore.pipeline.model.TaskType
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PipelineRegistryTest {
    
    private lateinit var photoAnalysisPipeline: PhotoAnalysisPipeline
    private lateinit var portfolioReviewPipeline: PortfolioReviewPipeline
    private lateinit var scoutingPipeline: ScoutingPipeline
    private lateinit var registry: PipelineRegistry
    
    @BeforeEach
    fun setUp() {
        photoAnalysisPipeline = mockk()
        portfolioReviewPipeline = mockk()
        scoutingPipeline = mockk()
        registry = PipelineRegistry(photoAnalysisPipeline, portfolioReviewPipeline, scoutingPipeline)
    }
    
    @Test
    fun `should get photo analysis pipeline`() {
        // When
        val pipeline = registry.getPipeline(TaskType.PHOTO_ANALYSIS)
        
        // Then
        assertThat(pipeline).isEqualTo(photoAnalysisPipeline)
    }
    
    @Test
    fun `should get portfolio review pipeline`() {
        // When
        val pipeline = registry.getPipeline(TaskType.PORTFOLIO_REVIEW)
        
        // Then
        assertThat(pipeline).isEqualTo(portfolioReviewPipeline)
    }
    
    @Test
    fun `should get scouting pipeline`() {
        // When
        val pipeline = registry.getPipeline(TaskType.SCOUTING)
        
        // Then
        assertThat(pipeline).isEqualTo(scoutingPipeline)
    }
    
    @Test
    fun `should get all task types`() {
        // When
        val taskTypes = registry.getAllTaskTypes()
        
        // Then
        assertThat(taskTypes).hasSize(3)
        assertThat(taskTypes).contains(
            TaskType.PHOTO_ANALYSIS,
            TaskType.PORTFOLIO_REVIEW,
            TaskType.SCOUTING
        )
    }
}

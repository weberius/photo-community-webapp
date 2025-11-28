package com.example.aicore.service

import com.example.aicore.TestDataBuilder
import com.example.aicore.persistence.entity.AiCost
import com.example.aicore.persistence.repository.AiCostRepository
import com.example.aicore.persistence.repository.AiLogRepository
import com.example.aicore.pipeline.PipelineRegistry
import com.example.aicore.pipeline.model.TaskType
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class AiOrchestrationServiceTest {
    
    private lateinit var pipelineRegistry: PipelineRegistry
    private lateinit var aiLogRepository: AiLogRepository
    private lateinit var aiCostRepository: AiCostRepository
    private lateinit var service: AiOrchestrationService
    
    @BeforeEach
    fun setUp() {
        pipelineRegistry = mockk()
        aiLogRepository = mockk()
        aiCostRepository = mockk()
        service = AiOrchestrationService(pipelineRegistry, aiLogRepository, aiCostRepository)
    }
    
    @Test
    fun `should execute AI task successfully and track costs`() = runBlocking {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext()
        val pipeline = mockk<com.example.aicore.pipeline.AiPipeline>()
        val pipelineResult = TestDataBuilder.buildAiExecutionResult(
            success = true,
            tokenCount = 1234,
            provider = "gemini"
        )
        
        every { pipelineRegistry.getPipeline(TaskType.PHOTO_ANALYSIS) } returns pipeline
        coEvery { pipeline.execute(context) } returns pipelineResult
        every { aiLogRepository.save(any()) } returnsArgument 0
        every { aiCostRepository.findByDateAndProviderAndTaskType(any(), any(), any()) } returns null
        every { aiCostRepository.save(any()) } returnsArgument 0
        
        // When
        val result = service.executeAiTask(context)
        
        // Then
        assertThat(result.success).isTrue()
        assertThat(result.tokenCount).isEqualTo(1234)
        assertThat(result.provider).isEqualTo("gemini")
        
        verify(exactly = 2) { aiLogRepository.save(any()) }
        verify { aiCostRepository.save(any()) }
    }
    
    @Test
    fun `should handle pipeline execution failure`() = runBlocking {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext()
        val pipeline = mockk<com.example.aicore.pipeline.AiPipeline>()
        
        every { pipelineRegistry.getPipeline(TaskType.PHOTO_ANALYSIS) } returns pipeline
        coEvery { pipeline.execute(context) } throws RuntimeException("Pipeline failed")
        every { aiLogRepository.save(any()) } returnsArgument 0
        
        // When
        val result = service.executeAiTask(context)
        
        // Then
        assertThat(result.success).isFalse()
        assertThat(result.errorMessage).isEqualTo("Pipeline failed")
        
        verify(exactly = 2) { aiLogRepository.save(any()) }
        verify(exactly = 0) { aiCostRepository.save(any()) }
    }
    
    @Test
    fun `should update existing cost record when tracking costs`() = runBlocking {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext()
        val pipeline = mockk<com.example.aicore.pipeline.AiPipeline>()
        val pipelineResult = TestDataBuilder.buildAiExecutionResult(tokenCount = 1000)
        
        val existingCost = TestDataBuilder.buildAiCost(
            totalTokens = 5000,
            requestCount = 5,
            estimatedCost = BigDecimal("0.0025")
        )
        
        every { pipelineRegistry.getPipeline(TaskType.PHOTO_ANALYSIS) } returns pipeline
        coEvery { pipeline.execute(context) } returns pipelineResult
        every { aiLogRepository.save(any()) } returnsArgument 0
        every { aiCostRepository.findByDateAndProviderAndTaskType(any(), any(), any()) } returns existingCost
        every { aiCostRepository.save(any()) } returnsArgument 0
        
        // When
        service.executeAiTask(context)
        
        // Then
        verify { 
            aiCostRepository.save(match<AiCost> { 
                it.totalTokens == 6000 && it.requestCount == 6
            })
        }
    }
    
    @Test
    fun `should calculate cost correctly for Gemini`() {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext(provider = "gemini")
        val pipeline = mockk<com.example.aicore.pipeline.AiPipeline>()
        val pipelineResult = TestDataBuilder.buildAiExecutionResult(
            tokenCount = 1000,
            provider = "gemini"
        )
        
        every { pipelineRegistry.getPipeline(TaskType.PHOTO_ANALYSIS) } returns pipeline
        coEvery { pipeline.execute(context) } returns pipelineResult
        every { aiLogRepository.save(any()) } returnsArgument 0
        every { aiCostRepository.findByDateAndProviderAndTaskType(any(), any(), any()) } returns null
        
        val savedCostSlot = slot<AiCost>()
        every { aiCostRepository.save(capture(savedCostSlot)) } returnsArgument 0
        
        // When
        runBlocking { service.executeAiTask(context) }
        
        // Then
        val savedCost = savedCostSlot.captured
        assertThat(savedCost.estimatedCost).isEqualByComparingTo(BigDecimal("0.0005"))
    }
    
    @Test
    fun `should calculate cost correctly for OpenAI`() {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext(provider = "openai")
        val pipeline = mockk<com.example.aicore.pipeline.AiPipeline>()
        val pipelineResult = TestDataBuilder.buildAiExecutionResult(
            tokenCount = 1000,
            provider = "openai"
        )
        
        every { pipelineRegistry.getPipeline(TaskType.PHOTO_ANALYSIS) } returns pipeline
        coEvery { pipeline.execute(context) } returns pipelineResult
        every { aiLogRepository.save(any()) } returnsArgument 0
        every { aiCostRepository.findByDateAndProviderAndTaskType(any(), any(), any()) } returns null
        
        val savedCostSlot = slot<AiCost>()
        every { aiCostRepository.save(capture(savedCostSlot)) } returnsArgument 0
        
        // When
        runBlocking { service.executeAiTask(context) }
        
        // Then
        val savedCost = savedCostSlot.captured
        assertThat(savedCost.estimatedCost).isEqualByComparingTo(BigDecimal("0.0300"))
    }
}

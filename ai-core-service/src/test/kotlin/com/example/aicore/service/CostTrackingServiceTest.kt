package com.example.aicore.service

import com.example.aicore.TestDataBuilder
import com.example.aicore.persistence.repository.AiCostRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class CostTrackingServiceTest {
    
    private lateinit var aiCostRepository: AiCostRepository
    private lateinit var service: CostTrackingService
    
    @BeforeEach
    fun setUp() {
        aiCostRepository = mockk()
        service = CostTrackingService(aiCostRepository)
    }
    
    @Test
    fun `should calculate cost summary for month period`() {
        // Given
        val costs = listOf(
            TestDataBuilder.buildAiCost(
                provider = "gemini",
                taskType = "PHOTO_ANALYSIS",
                totalTokens = 50000,
                requestCount = 20,
                estimatedCost = BigDecimal("0.25")
            ),
            TestDataBuilder.buildAiCost(
                provider = "openai",
                taskType = "PORTFOLIO_REVIEW",
                totalTokens = 75000,
                requestCount = 22,
                estimatedCost = BigDecimal("3.50")
            )
        )
        
        every { aiCostRepository.findCostsByDateRange(any(), any()) } returns costs
        
        // When
        val summary = service.getCostSummary("month")
        
        // Then
        assertThat(summary.period).isEqualTo("month")
        assertThat(summary.totalTokens).isEqualTo(125000L)
        assertThat(summary.totalCost).isEqualByComparingTo(BigDecimal("3.75"))
        assertThat(summary.requestCount).isEqualTo(42)
        assertThat(summary.breakdown).hasSize(2)
    }
    
    @Test
    fun `should calculate cost summary for week period`() {
        // Given
        val costs = listOf(
            TestDataBuilder.buildAiCost(totalTokens = 25000, requestCount = 10)
        )
        
        every { aiCostRepository.findCostsByDateRange(any(), any()) } returns costs
        
        // When
        val summary = service.getCostSummary("week")
        
        // Then
        assertThat(summary.period).isEqualTo("week")
        assertThat(summary.startDate).isEqualTo(LocalDate.now().minusWeeks(1))
        assertThat(summary.endDate).isEqualTo(LocalDate.now())
    }
    
    @Test
    fun `should calculate cost summary for day period`() {
        // Given
        every { aiCostRepository.findCostsByDateRange(any(), any()) } returns emptyList()
        
        // When
        val summary = service.getCostSummary("day")
        
        // Then
        assertThat(summary.period).isEqualTo("day")
        assertThat(summary.startDate).isEqualTo(LocalDate.now())
        assertThat(summary.endDate).isEqualTo(LocalDate.now())
        assertThat(summary.totalTokens).isEqualTo(0L)
    }
    
    @Test
    fun `should calculate cost summary for year period`() {
        // Given
        every { aiCostRepository.findCostsByDateRange(any(), any()) } returns emptyList()
        
        // When
        val summary = service.getCostSummary("year")
        
        // Then
        assertThat(summary.period).isEqualTo("year")
        assertThat(summary.startDate).isEqualTo(LocalDate.now().minusYears(1))
    }
    
    @Test
    fun `should use month as default period for unknown period`() {
        // Given
        every { aiCostRepository.findCostsByDateRange(any(), any()) } returns emptyList()
        
        // When
        val summary = service.getCostSummary("invalid")
        
        // Then
        assertThat(summary.period).isEqualTo("invalid")
        assertThat(summary.startDate).isEqualTo(LocalDate.now().minusMonths(1))
    }
    
    @Test
    fun `should create breakdown by provider and task type`() {
        // Given
        val costs = listOf(
            TestDataBuilder.buildAiCost(
                provider = "gemini",
                taskType = "PHOTO_ANALYSIS",
                totalTokens = 1000,
                requestCount = 5,
                estimatedCost = BigDecimal("0.50")
            ),
            TestDataBuilder.buildAiCost(
                provider = "gemini",
                taskType = "SCOUTING",
                totalTokens = 2000,
                requestCount = 3,
                estimatedCost = BigDecimal("1.00")
            )
        )
        
        every { aiCostRepository.findCostsByDateRange(any(), any()) } returns costs
        
        // When
        val summary = service.getCostSummary("month")
        
        // Then
        assertThat(summary.breakdown).hasSize(2)
        assertThat(summary.breakdown[0].provider).isEqualTo("gemini")
        assertThat(summary.breakdown[0].taskType).isEqualTo("PHOTO_ANALYSIS")
        assertThat(summary.breakdown[1].taskType).isEqualTo("SCOUTING")
    }
}

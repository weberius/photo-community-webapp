package com.example.aicore.controller

import com.example.aicore.service.CostTrackingService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(CostController::class)
class CostControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockkBean
    private lateinit var costTrackingService: CostTrackingService
    
    @Test
    fun `should get cost summary for default period`() {
        // Given
        val summary = CostTrackingService.CostSummary(
            period = "month",
            startDate = LocalDate.now().minusMonths(1),
            endDate = LocalDate.now(),
            totalTokens = 125000L,
            totalCost = BigDecimal("3.75"),
            requestCount = 42,
            breakdown = listOf(
                CostTrackingService.CostBreakdown(
                    provider = "gemini",
                    taskType = "PHOTO_ANALYSIS",
                    tokens = 50000,
                    cost = BigDecimal("0.25"),
                    requests = 20
                )
            )
        )
        
        every { costTrackingService.getCostSummary("month") } returns summary
        
        // When & Then
        mockMvc.get("/api/v1/ai/costs")
            .andExpect {
                status { isOk() }
                jsonPath("$.period") { value("month") }
                jsonPath("$.totalTokens") { value(125000) }
                jsonPath("$.totalCost") { value(3.75) }
                jsonPath("$.requestCount") { value(42) }
                jsonPath("$.breakdown[0].provider") { value("gemini") }
            }
    }
    
    @Test
    fun `should get cost summary for specific period`() {
        // Given
        val summary = CostTrackingService.CostSummary(
            period = "week",
            startDate = LocalDate.now().minusWeeks(1),
            endDate = LocalDate.now(),
            totalTokens = 25000L,
            totalCost = BigDecimal("0.75"),
            requestCount = 10,
            breakdown = emptyList()
        )
        
        every { costTrackingService.getCostSummary("week") } returns summary
        
        // When & Then
        mockMvc.get("/api/v1/ai/costs?period=week")
            .andExpect {
                status { isOk() }
                jsonPath("$.period") { value("week") }
                jsonPath("$.totalTokens") { value(25000) }
            }
    }
    
    @Test
    fun `should get cost summary for day period`() {
        // Given
        val summary = CostTrackingService.CostSummary(
            period = "day",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            totalTokens = 5000L,
            totalCost = BigDecimal("0.15"),
            requestCount = 2,
            breakdown = emptyList()
        )
        
        every { costTrackingService.getCostSummary("day") } returns summary
        
        // When & Then
        mockMvc.get("/api/v1/ai/costs?period=day")
            .andExpect {
                status { isOk() }
                jsonPath("$.period") { value("day") }
            }
    }
}

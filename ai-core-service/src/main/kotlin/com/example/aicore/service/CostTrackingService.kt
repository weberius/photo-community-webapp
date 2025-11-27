package com.example.aicore.service

import com.example.aicore.persistence.repository.AiCostRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class CostTrackingService(
    private val aiCostRepository: AiCostRepository
) {
    
    data class CostSummary(
        val period: String,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val totalTokens: Long,
        val totalCost: BigDecimal,
        val requestCount: Int,
        val breakdown: List<CostBreakdown>
    )
    
    data class CostBreakdown(
        val provider: String,
        val taskType: String,
        val tokens: Int,
        val cost: BigDecimal,
        val requests: Int
    )
    
    fun getCostSummary(period: String): CostSummary {
        val (startDate, endDate) = getDateRange(period)
        
        val costs = aiCostRepository.findCostsByDateRange(startDate, endDate)
        
        val totalTokens = costs.sumOf { it.totalTokens.toLong() }
        val totalCost = costs.sumOf { it.estimatedCost }
        val totalRequests = costs.sumOf { it.requestCount }
        
        val breakdown = costs.map { cost ->
            CostBreakdown(
                provider = cost.provider,
                taskType = cost.taskType,
                tokens = cost.totalTokens,
                cost = cost.estimatedCost,
                requests = cost.requestCount
            )
        }
        
        return CostSummary(
            period = period,
            startDate = startDate,
            endDate = endDate,
            totalTokens = totalTokens,
            totalCost = totalCost,
            requestCount = totalRequests,
            breakdown = breakdown
        )
    }
    
    private fun getDateRange(period: String): Pair<LocalDate, LocalDate> {
        val endDate = LocalDate.now()
        val startDate = when (period.lowercase()) {
            "day" -> endDate
            "week" -> endDate.minusWeeks(1)
            "month" -> endDate.minusMonths(1)
            "year" -> endDate.minusYears(1)
            else -> endDate.minusMonths(1) // default to month
        }
        return Pair(startDate, endDate)
    }
}

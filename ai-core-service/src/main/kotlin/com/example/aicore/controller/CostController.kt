package com.example.aicore.controller

import com.example.aicore.service.CostTrackingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/ai/costs")
@Tag(name = "Cost Tracking", description = "Track AI usage costs")
class CostController(
    private val costTrackingService: CostTrackingService
) {
    
    @GetMapping
    @Operation(
        summary = "Get cost summary",
        description = "Get cost summary for the specified period (day, week, month, year)"
    )
    fun getCostSummary(
        @RequestParam(defaultValue = "month") period: String
    ): ResponseEntity<CostTrackingService.CostSummary> {
        val summary = costTrackingService.getCostSummary(period)
        return ResponseEntity.ok(summary)
    }
}

package com.example.aicore.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "ai_costs",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["date", "provider", "task_type"])
    ]
)
data class AiCost(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false)
    val date: LocalDate,
    
    @Column(nullable = false, length = 50)
    val provider: String,
    
    @Column(name = "task_type", nullable = false, length = 50)
    val taskType: String,
    
    @Column(name = "total_tokens", nullable = false)
    var totalTokens: Int = 0,
    
    @Column(name = "request_count", nullable = false)
    var requestCount: Int = 0,
    
    @Column(name = "estimated_cost", nullable = false, precision = 10, scale = 4)
    var estimatedCost: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

package com.example.aicore.persistence.repository

import com.example.aicore.persistence.entity.AiCost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface AiCostRepository : JpaRepository<AiCost, UUID> {
    
    fun findByDate(date: LocalDate): List<AiCost>
    
    fun findByDateBetween(startDate: LocalDate, endDate: LocalDate): List<AiCost>
    
    fun findByDateAndProviderAndTaskType(
        date: LocalDate,
        provider: String,
        taskType: String
    ): AiCost?
    
    @Query("""
        SELECT c FROM AiCost c 
        WHERE c.date BETWEEN :startDate AND :endDate 
        ORDER BY c.date DESC
    """)
    fun findCostsByDateRange(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<AiCost>
    
    @Query("""
        SELECT SUM(c.totalTokens) FROM AiCost c 
        WHERE c.date BETWEEN :startDate AND :endDate
    """)
    fun sumTokensByDateRange(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): Long?
    
    @Query("""
        SELECT SUM(c.estimatedCost) FROM AiCost c 
        WHERE c.date BETWEEN :startDate AND :endDate
    """)
    fun sumCostByDateRange(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): java.math.BigDecimal?
}

package com.example.aicore.persistence.repository

import com.example.aicore.persistence.entity.AiLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface AiLogRepository : JpaRepository<AiLog, UUID> {
    
    fun findByTaskType(taskType: String): List<AiLog>
    
    fun findBySuccessOrderByRequestTimestampDesc(success: Boolean): List<AiLog>
    
    fun findByRequestTimestampBetween(start: LocalDateTime, end: LocalDateTime): List<AiLog>
    
    @Query("SELECT COUNT(l) FROM AiLog l WHERE l.success = true")
    fun countSuccessfulRequests(): Long
    
    @Query("SELECT COUNT(l) FROM AiLog l WHERE l.success = false")
    fun countFailedRequests(): Long
}

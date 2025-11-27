package com.example.aicore.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "ai_logs")
data class AiLog(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "task_type", nullable = false, length = 50)
    val taskType: String,
    
    @Column(name = "request_timestamp", nullable = false)
    val requestTimestamp: LocalDateTime,
    
    @Column(name = "response_timestamp")
    var responseTimestamp: LocalDateTime? = null,
    
    @Column(name = "token_count")
    var tokenCount: Int? = null,
    
    @Column(nullable = false)
    var success: Boolean = false,
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    var errorMessage: String? = null,
    
    @Column(length = 50)
    var provider: String? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

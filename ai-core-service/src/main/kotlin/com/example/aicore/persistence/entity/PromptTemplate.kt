package com.example.aicore.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "prompt_templates")
data class PromptTemplate(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false, unique = true, length = 100)
    val name: String,
    
    @Column(nullable = false, length = 20)
    var version: String,
    
    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,
    
    @Column(columnDefinition = "TEXT")
    var parameters: String? = null,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

package com.example.aicore.persistence.repository

import com.example.aicore.persistence.entity.PromptTemplate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PromptTemplateRepository : JpaRepository<PromptTemplate, UUID> {
    
    fun findByName(name: String): PromptTemplate?
    
    fun findByActiveTrue(): List<PromptTemplate>
    
    fun existsByName(name: String): Boolean
}

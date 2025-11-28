package com.example.aicore.persistence.repository

import com.example.aicore.TestDataBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class PromptTemplateRepositoryTest {
    
    @Autowired
    private lateinit var repository: PromptTemplateRepository
    
    @Test
    fun `should save and retrieve prompt template`() {
        // Given
        val template = TestDataBuilder.buildPromptTemplate()
        
        // When
        val saved = repository.save(template)
        val found = repository.findById(saved.id)
        
        // Then
        assertThat(found).isPresent
        assertThat(found.get().name).isEqualTo(template.name)
        assertThat(found.get().content).isEqualTo(template.content)
    }
    
    @Test
    fun `should find template by name`() {
        // Given
        val template = TestDataBuilder.buildPromptTemplate(name = "custom-template")
        repository.save(template)
        
        // When
        val found = repository.findByName("custom-template")
        
        // Then
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo("custom-template")
    }
    
    @Test
    fun `should return null when template not found by name`() {
        // When
        val found = repository.findByName("nonexistent")
        
        // Then
        assertThat(found).isNull()
    }
    
    @Test
    fun `should update template content`() {
        // Given
        val template = TestDataBuilder.buildPromptTemplate()
        val saved = repository.save(template)
        
        // When
        saved.content = "Updated content"
        repository.save(saved)
        
        val updated = repository.findById(saved.id).get()
        
        // Then
        assertThat(updated.content).isEqualTo("Updated content")
    }
    
    @Test
    fun `should delete template`() {
        // Given
        val template = TestDataBuilder.buildPromptTemplate()
        val saved = repository.save(template)
        
        // When
        repository.deleteById(saved.id)
        
        // Then
        val found = repository.findById(saved.id)
        assertThat(found).isEmpty
    }
}

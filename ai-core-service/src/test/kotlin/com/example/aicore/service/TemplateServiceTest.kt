package com.example.aicore.service

import com.example.aicore.TestDataBuilder
import com.example.aicore.templates.TemplateRegistry
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TemplateServiceTest {
    
    private lateinit var templateRegistry: TemplateRegistry
    private lateinit var service: TemplateService
    
    @BeforeEach
    fun setUp() {
        templateRegistry = mockk()
        service = TemplateService(templateRegistry)
    }
    
    @Test
    fun `should get template by name`() {
        // Given
        val template = TestDataBuilder.buildTemplateModel(name = "photo-analysis")
        every { templateRegistry.getTemplate("photo-analysis") } returns template
        
        // When
        val result = service.getTemplate("photo-analysis")
        
        // Then
        assertThat(result.name).isEqualTo("photo-analysis")
        verify { templateRegistry.getTemplate("photo-analysis") }
    }
    
    @Test
    fun `should get all template names`() {
        // Given
        val names = listOf("photo-analysis", "portfolio-review", "scouting")
        every { templateRegistry.getAllTemplateNames() } returns names
        
        // When
        val result = service.getAllTemplateNames()
        
        // Then
        assertThat(result).hasSize(3)
        assertThat(result).contains("photo-analysis", "portfolio-review", "scouting")
    }
    
    @Test
    fun `should override template`() {
        // Given
        every { templateRegistry.overrideTemplate("photo-analysis", any()) } just runs
        
        // When
        service.overrideTemplate("photo-analysis", "New content")
        
        // Then
        verify { templateRegistry.overrideTemplate("photo-analysis", "New content") }
    }
    
    @Test
    fun `should clear override`() {
        // Given
        every { templateRegistry.clearOverride("photo-analysis") } just runs
        
        // When
        service.clearOverride("photo-analysis")
        
        // Then
        verify { templateRegistry.clearOverride("photo-analysis") }
    }
    
    @Test
    fun `should check if template has override`() {
        // Given
        every { templateRegistry.hasOverride("photo-analysis") } returns true
        
        // When
        val result = service.hasOverride("photo-analysis")
        
        // Then
        assertThat(result).isTrue()
        verify { templateRegistry.hasOverride("photo-analysis") }
    }
}

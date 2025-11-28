package com.example.aicore.templates

import com.example.aicore.exception.TemplateNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class TemplateRegistryTest {
    
    @Autowired
    private lateinit var templateRegistry: TemplateRegistry
    
    @BeforeEach
    fun setUp() {
        // Clear any overrides from previous tests
        templateRegistry.getAllTemplateNames().forEach { name ->
            if (templateRegistry.hasOverride(name)) {
                templateRegistry.clearOverride(name)
            }
        }
    }
    
    @Test
    fun `should load templates from YAML files`() {
        // When
        val templateNames = templateRegistry.getAllTemplateNames()
        
        // Then
        assertThat(templateNames).isNotEmpty
        assertThat(templateNames).contains("photo-analysis", "portfolio-review", "scouting")
    }
    
    @Test
    fun `should get template by name`() {
        // When
        val template = templateRegistry.getTemplate("photo-analysis")
        
        // Then
        assertThat(template.name).isEqualTo("photo-analysis")
        assertThat(template.content).isNotBlank()
        assertThat(template.version).isNotBlank()
    }
    
    @Test
    fun `should throw exception for non-existent template`() {
        // When & Then
        assertThatThrownBy {
            templateRegistry.getTemplate("non-existent")
        }.isInstanceOf(TemplateNotFoundException::class.java)
            .hasMessageContaining("Template not found: non-existent")
    }
    
    @Test
    fun `should render template with parameters`() {
        // Given
        val params = mapOf(
            "photographerName" to "Alice",
            "genre" to "Landscape"
        )
        
        // When
        val rendered = templateRegistry.renderTemplate("photo-analysis", params)
        
        // Then
        assertThat(rendered).contains("Alice")
        assertThat(rendered).contains("Landscape")
        assertThat(rendered).doesNotContain("{{photographerName}}")
        assertThat(rendered).doesNotContain("{{genre}}")
    }
    
    @Test
    fun `should override template content`() {
        // Given
        val originalTemplate = templateRegistry.getTemplate("photo-analysis")
        val newContent = "This is a custom template with {{param}}"
        
        // When
        templateRegistry.overrideTemplate("photo-analysis", newContent)
        
        // Then
        assertThat(templateRegistry.hasOverride("photo-analysis")).isTrue()
        
        val rendered = templateRegistry.renderTemplate("photo-analysis", mapOf("param" to "value"))
        assertThat(rendered).isEqualTo("This is a custom template with value")
    }
    
    @Test
    fun `should clear template override`() {
        // Given
        templateRegistry.overrideTemplate("photo-analysis", "Custom content")
        assertThat(templateRegistry.hasOverride("photo-analysis")).isTrue()
        
        // When
        templateRegistry.clearOverride("photo-analysis")
        
        // Then
        assertThat(templateRegistry.hasOverride("photo-analysis")).isFalse()
    }
    
    @Test
    fun `should use original content after clearing override`() {
        // Given
        val originalTemplate = templateRegistry.getTemplate("photo-analysis")
        templateRegistry.overrideTemplate("photo-analysis", "Override content")
        
        // When
        templateRegistry.clearOverride("photo-analysis")
        val rendered = templateRegistry.renderTemplate("photo-analysis", mapOf())
        
        // Then
        assertThat(rendered).contains(originalTemplate.content.take(50))
    }
    
    @Test
    fun `should throw exception when overriding non-existent template`() {
        // When & Then
        assertThatThrownBy {
            templateRegistry.overrideTemplate("non-existent", "content")
        }.isInstanceOf(TemplateNotFoundException::class.java)
    }
}

package com.example.aicore.controller

import com.example.aicore.TestDataBuilder
import com.example.aicore.controller.dto.TemplateOverrideRequest
import com.example.aicore.service.TemplateService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@WebMvcTest(TemplateController::class)
class TemplateControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean
    private lateinit var templateService: TemplateService
    
    @Test
    fun `should list all templates`() {
        // Given
        val templateNames = listOf("photo-analysis", "portfolio-review", "scouting")
        every { templateService.getAllTemplateNames() } returns templateNames
        
        // When & Then
        mockMvc.get("/api/v1/templates")
            .andExpect {
                status { isOk() }
                jsonPath("$[0]") { value("photo-analysis") }
                jsonPath("$[1]") { value("portfolio-review") }
                jsonPath("$[2]") { value("scouting") }
            }
    }
    
    @Test
    fun `should get template by name`() {
        // Given
        val template = TestDataBuilder.buildTemplateModel(
            name = "photo-analysis",
            version = "1.0",
            description = "Photo analysis template"
        )
        
        every { templateService.getTemplate("photo-analysis") } returns template
        every { templateService.hasOverride("photo-analysis") } returns false
        
        // When & Then
        mockMvc.get("/api/v1/templates/photo-analysis")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("photo-analysis") }
                jsonPath("$.version") { value("1.0") }
                jsonPath("$.hasOverride") { value(false) }
            }
    }
    
    @Test
    fun `should get template with override`() {
        // Given
        val template = TestDataBuilder.buildTemplateModel(name = "photo-analysis")
        
        every { templateService.getTemplate("photo-analysis") } returns template
        every { templateService.hasOverride("photo-analysis") } returns true
        
        // When & Then
        mockMvc.get("/api/v1/templates/photo-analysis")
            .andExpect {
                status { isOk() }
                jsonPath("$.hasOverride") { value(true) }
            }
    }
    
    @Test
    fun `should override template`() {
        // Given
        val request = TemplateOverrideRequest(content = "New custom template content")
        every { templateService.overrideTemplate("photo-analysis", any()) } just runs
        
        // When & Then
        mockMvc.put("/api/v1/templates/photo-analysis") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.message") { value("Template override set for: photo-analysis") }
        }
        
        verify { templateService.overrideTemplate("photo-analysis", "New custom template content") }
    }
    
    @Test
    fun `should clear template override`() {
        // Given
        every { templateService.clearOverride("photo-analysis") } just runs
        
        // When & Then
        mockMvc.delete("/api/v1/templates/photo-analysis/override")
            .andExpect {
                status { isOk() }
                jsonPath("$.message") { value("Template override cleared for: photo-analysis") }
            }
        
        verify { templateService.clearOverride("photo-analysis") }
    }
}

package com.example.aicore.controller

import com.example.aicore.TestDataBuilder
import com.example.aicore.service.AiOrchestrationService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(AiController::class)
class AiControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean
    private lateinit var aiOrchestrationService: AiOrchestrationService
    
    @Test
    fun `should execute AI task successfully`() {
        // Given
        val request = TestDataBuilder.buildAiExecuteRequest()
        val result = TestDataBuilder.buildAiExecutionResult()
        
        every { aiOrchestrationService.executeAiTask(any()) } returns result
        
        // When & Then
        mockMvc.post("/api/v1/ai/execute") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.content") { value(result.content) }
            jsonPath("$.tokenCount") { value(result.tokenCount) }
            jsonPath("$.provider") { value(result.provider) }
        }
    }
    
    @Test
    fun `should handle execution failure`() {
        // Given
        val request = TestDataBuilder.buildAiExecuteRequest()
        val result = TestDataBuilder.buildAiExecutionResult(
            success = false,
            content = null,
            errorMessage = "AI provider error"
        )
        
        every { aiOrchestrationService.executeAiTask(any()) } returns result
        
        // When & Then
        mockMvc.post("/api/v1/ai/execute") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(false) }
            jsonPath("$.errorMessage") { value("AI provider error") }
        }
    }
    
    @Test
    fun `should handle different task types`() {
        // Given
        val request = TestDataBuilder.buildAiExecuteRequest(
            taskType = com.example.aicore.pipeline.model.TaskType.PORTFOLIO_REVIEW
        )
        val result = TestDataBuilder.buildAiExecutionResult()
        
        every { aiOrchestrationService.executeAiTask(any()) } returns result
        
        // When & Then
        mockMvc.post("/api/v1/ai/execute") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
        }
    }
}

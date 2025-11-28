package com.example.aicore.pipeline

import com.example.aicore.TestDataBuilder
import com.example.aicore.adapter.AiClientFactory
import com.example.aicore.adapter.AiModelClient
import com.example.aicore.templates.TemplateRegistry
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PhotoAnalysisPipelineTest {
    
    private lateinit var templateRegistry: TemplateRegistry
    private lateinit var aiClientFactory: AiClientFactory
    private lateinit var aiClient: AiModelClient
    private lateinit var pipeline: PhotoAnalysisPipeline
    
    @BeforeEach
    fun setUp() {
        templateRegistry = mockk()
        aiClientFactory = mockk()
        aiClient = mockk()
        pipeline = PhotoAnalysisPipeline(templateRegistry, aiClientFactory)
    }
    
    @Test
    fun `should execute photo analysis successfully`() = runBlocking {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext()
        val renderedPrompt = "Analyze this photo for Test in Portrait style"
        val aiResponse = TestDataBuilder.buildAiResponse()
        
        every { templateRegistry.renderTemplate("photo-analysis", any()) } returns renderedPrompt
        every { aiClientFactory.getClient("gemini") } returns aiClient
        every { aiClient.getProviderName() } returns "gemini"
        coEvery { aiClient.generate(any()) } returns aiResponse
        
        // When
        val result = pipeline.execute(context)
        
        // Then
        assertThat(result.success).isTrue()
        assertThat(result.content).isEqualTo(aiResponse.content)
        assertThat(result.tokenCount).isEqualTo(aiResponse.tokenCount)
        assertThat(result.provider).isEqualTo("gemini")
    }
    
    @Test
    fun `should handle AI client failure`() = runBlocking {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext()
        
        every { templateRegistry.renderTemplate("photo-analysis", any()) } returns "prompt"
        every { aiClientFactory.getClient("gemini") } returns aiClient
        coEvery { aiClient.generate(any()) } throws RuntimeException("AI API failed")
        
        // When
        val result = pipeline.execute(context)
        
        // Then
        assertThat(result.success).isFalse()
        assertThat(result.errorMessage).isEqualTo("AI API failed")
    }
    
    @Test
    fun `should render template with correct parameters`() = runBlocking {
        // Given
        val context = TestDataBuilder.buildAiExecutionContext(
            promptParams = mapOf("photographerName" to "Alice", "genre" to "Landscape")
        )
        val aiResponse = TestDataBuilder.buildAiResponse()
        
        every { 
            templateRegistry.renderTemplate("photo-analysis", mapOf("photographerName" to "Alice", "genre" to "Landscape")) 
        } returns "rendered prompt"
        every { aiClientFactory.getClient(any()) } returns aiClient
        every { aiClient.getProviderName() } returns "gemini"
        coEvery { aiClient.generate(any()) } returns aiResponse
        
        // When
        val result = pipeline.execute(context)
        
        // Then
        assertThat(result.success).isTrue()
    }
    
    @Test
    fun `should return correct template name`() {
        assertThat(pipeline.getTemplateName()).isEqualTo("photo-analysis")
    }
}

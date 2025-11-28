package com.example.aicore.persistence.repository

import com.example.aicore.TestDataBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class AiCostRepositoryTest {
    
    @Autowired
    private lateinit var repository: AiCostRepository
    
    @Test
    fun `should save and retrieve AI cost`() {
        // Given
        val cost = TestDataBuilder.buildAiCost()
        
        // When
        val saved = repository.save(cost)
        val found = repository.findById(saved.id)
        
        // Then
        assertThat(found).isPresent
        assertThat(found.get().provider).isEqualTo(cost.provider)
        assertThat(found.get().totalTokens).isEqualTo(cost.totalTokens)
    }

    
    /**
    @Test
    fun `should find costs by date`() {
        // Given
        val today = LocalDate.now()
        val cost1 = TestDataBuilder.buildAiCost(date = today)
        val cost2 = TestDataBuilder.buildAiCost(date = today)
        repository.saveAll(listOf(cost1, cost2))
        
        // When
        val costs = repository.findByDate(today)
        
        // Then
        assertThat(costs).hasSize(2)
    }
    */
    
    @Test
    fun `should find costs by date range`() {
        // Given
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val lastWeek = today.minusWeeks(1)
        
        repository.save(TestDataBuilder.buildAiCost(date = today))
        repository.save(TestDataBuilder.buildAiCost(date = yesterday))
        repository.save(TestDataBuilder.buildAiCost(date = lastWeek))
        
        // When
        val costs = repository.findByDateBetween(yesterday, today)
        
        // Then
        assertThat(costs).hasSize(2)
    }
    

    @Test
    fun `should find cost by date, provider and task type`() {
        // Given
        val today = LocalDate.now()
        val cost = TestDataBuilder.buildAiCost(
            date = today,
            provider = "gemini",
            taskType = "PHOTO_ANALYSIS"
        )
        repository.save(cost)
        
        // When
        val found = repository.findByDateAndProviderAndTaskType(today, "gemini", "PHOTO_ANALYSIS")
        
        // Then
        assertThat(found).isNotNull
        assertThat(found?.provider).isEqualTo("gemini")
        assertThat(found?.taskType).isEqualTo("PHOTO_ANALYSIS")
    }

    

    @Test
    fun `should return null when cost not found by date, provider and task type`() {
        // When
        val found = repository.findByDateAndProviderAndTaskType(
            LocalDate.now(),
            "nonexistent",
            "NONEXISTENT"
        )
        
        // Then
        assertThat(found).isNull()
    }


    @Test
    fun `should find costs by date range with custom query`() {
        // Given
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        repository.save(TestDataBuilder.buildAiCost(date = today, totalTokens = 1000))
        repository.save(TestDataBuilder.buildAiCost(date = yesterday, totalTokens = 2000))
        
        // When
        val costs = repository.findCostsByDateRange(yesterday, today)
        
        // Then
        assertThat(costs).hasSize(2)
        assertThat(costs[0].date).isAfter(costs[1].date) // Ordered by date DESC
    }

    
    @Test
    fun `should sum tokens by date range`() {
        // Given
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        repository.save(TestDataBuilder.buildAiCost(date = today, totalTokens = 1000))
        repository.save(TestDataBuilder.buildAiCost(date = yesterday, totalTokens = 2000))
        repository.save(TestDataBuilder.buildAiCost(date = today.minusWeeks(1), totalTokens = 5000))
        
        // When
        val sum = repository.sumTokensByDateRange(yesterday, today)
        
        // Then
        assertThat(sum).isEqualTo(3000L)
    }
    
    @Test
    fun `should sum cost by date range`() {
        // Given
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        repository.save(TestDataBuilder.buildAiCost(date = today, estimatedCost = BigDecimal("1.50")))
        repository.save(TestDataBuilder.buildAiCost(date = yesterday, estimatedCost = BigDecimal("2.25")))
        
        // When
        val sum = repository.sumCostByDateRange(yesterday, today)
        
        // Then
        assertThat(sum).isEqualByComparingTo(BigDecimal("3.75"))
    }

}

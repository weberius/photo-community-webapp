package com.example.aicore.persistence.repository

import com.example.aicore.TestDataBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
class AiLogRepositoryTest {
    
    @Autowired
    private lateinit var repository: AiLogRepository
    
    @Test
    fun `should save and retrieve AI log`() {
        // Given
        val log = TestDataBuilder.buildAiLog()
        
        // When
        val saved = repository.save(log)
        val found = repository.findById(saved.id)
        
        // Then
        assertThat(found).isPresent
        assertThat(found.get().taskType).isEqualTo(log.taskType)
        assertThat(found.get().provider).isEqualTo(log.provider)
        assertThat(found.get().success).isEqualTo(log.success)
    }
    
    @Test
    fun `should find logs by task type`() {
        // Given
        repository.save(TestDataBuilder.buildAiLog(taskType = "PHOTO_ANALYSIS"))
        repository.save(TestDataBuilder.buildAiLog(taskType = "PHOTO_ANALYSIS"))
        repository.save(TestDataBuilder.buildAiLog(taskType = "SCOUTING"))
        
        // When
        val logs = repository.findByTaskType("PHOTO_ANALYSIS")
        
        // Then
        assertThat(logs).hasSize(2)
        assertThat(logs).allMatch { it.taskType == "PHOTO_ANALYSIS" }
    }
    
    @Test
    fun `should find logs by success status`() {
        // Given
        repository.save(TestDataBuilder.buildAiLog(success = true))
        repository.save(TestDataBuilder.buildAiLog(success = true))
        repository.save(TestDataBuilder.buildAiLog(success = false))
        
        // When
        val successLogs = repository.findBySuccessOrderByRequestTimestampDesc(true)
        val failedLogs = repository.findBySuccessOrderByRequestTimestampDesc(false)
        
        // Then
        assertThat(successLogs).hasSize(2)
        assertThat(failedLogs).hasSize(1)
    }
    
    @Test
    fun `should find logs by timestamp range`() {
        // Given
        val now = LocalDateTime.now()
        val hourAgo = now.minusHours(1)
        val dayAgo = now.minusDays(1)
        
        repository.save(TestDataBuilder.buildAiLog(requestTimestamp = now))
        repository.save(TestDataBuilder.buildAiLog(requestTimestamp = hourAgo))
        repository.save(TestDataBuilder.buildAiLog(requestTimestamp = dayAgo))
        
        // When
        val logs = repository.findByRequestTimestampBetween(hourAgo.minusMinutes(1), now.plusMinutes(1))
        
        // Then
        assertThat(logs).hasSize(2)
    }
    
    @Test
    fun `should update log with response data`() {
        // Given
        val log = TestDataBuilder.buildAiLog(
            responseTimestamp = null,
            tokenCount = 0
        )
        val saved = repository.save(log)
        
        // When
        saved.responseTimestamp = LocalDateTime.now()
        saved.tokenCount = 1234
        saved.success = true
        repository.save(saved)
        
        val updated = repository.findById(saved.id).get()
        
        // Then
        assertThat(updated.responseTimestamp).isNotNull()
        assertThat(updated.tokenCount).isEqualTo(1234)
        assertThat(updated.success).isTrue()
    }
}
